package OS.Yanwit.service.feed_heater;

import OS.Yanwit.kafka.event.feed_heater.FeedHeaterEvent;
import OS.Yanwit.kafka.producer.feed_heater.FeedHeaterProducer;
import OS.Yanwit.mapper.PostMapper;
import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.redis.cache.service.author.AuthorCacheService;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import OS.Yanwit.repository.PostRepository;
import OS.Yanwit.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedHeaterServiceImpl implements FeedHeaterService {

    @Value("${batches.feed-heater.size}")
    private int usersBatchSize;
    @Value("${spring.data.redis.cache.settings.max-feed-size}")
    private int maxFeedSize;

    private final AuthorCacheService authorCacheService;
    private final UserService userService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostCacheService postCacheService;
    private final FeedHeaterProducer feedHeaterProducer;
    private final FeedCacheService feedCacheService;

    @Override
    public void heatUp() {

        List<UserDto> users = userService.getAllUsers();

        List<Map<Long, List<Long>>> batches = ListUtils.partition(users, usersBatchSize)
                .stream()
                .map(usersBatch -> usersBatch.stream()
                        .collect(Collectors.toMap(UserDto::getId, UserDto::getFolloweesIds)
                        ))
                .toList();

        for (Map<Long, List<Long>> batch : batches) {
            feedHeaterProducer.produce(new FeedHeaterEvent(batch));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void handleBatch(Map<Long, List<Long>> userBatch) {

        for (Long userId : userBatch.keySet()) {
            authorCacheService.save(userId);

            List<Long> followeesIds = userBatch.get(userId);
            postRepository.findByAuthorsAndLimit(followeesIds, maxFeedSize).stream()
                    .map(postMapper::toPostCache)
                    .forEach(postCache -> {
                        postCacheService.save(postCache);
                        feedCacheService.addPostIdToFollowerFeed(postCache.getId(), userId);
                    });
        }
    }
}

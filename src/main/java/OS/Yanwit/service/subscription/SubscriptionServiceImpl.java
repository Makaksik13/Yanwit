package OS.Yanwit.service.subscription;

import OS.Yanwit.kafka.producer.subscription.SubscriptionProducer;
import OS.Yanwit.mapper.PostMapper;
import OS.Yanwit.mapper.SubscriptionMapper;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.model.dto.PostDto;
import OS.Yanwit.model.dto.SubscriptionRequestDto;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import OS.Yanwit.repository.SubscriptionRepository;
import OS.Yanwit.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    @Value("${batches.HotPostsWhenSubscription.size}")
    private long batchSizeForHotPostsWhenSubscription;

    private final SubscriptionRepository subscriptionRepository;
    private final PostCacheService postCacheService;
    private final FeedCacheService feedCacheService;
    private final PostMapper postMapper;
    private final PostService postService;
    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionProducer subscriptionProducer;

    @Override
    @Transactional
    public void followUser(SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionRepository.followUser(subscriptionRequestDto.getFollowerId(), subscriptionRequestDto.getFolloweeId());
        List<PostDto> postDtoList = postService.findLatestByAuthor(subscriptionRequestDto.getFolloweeId(),
                batchSizeForHotPostsWhenSubscription);

        subscriptionProducer.produce(subscriptionMapper.toEvent(subscriptionRequestDto, OperationType.ADD));

        postCacheService.saveAll(postMapper.toListPostCacheFromDto(postDtoList));
        feedCacheService.addPostIdToFollowerFeedByBatch(postDtoList.stream().map(PostDto::getId).toList(),
                subscriptionRequestDto.getFollowerId());
    }

    @Override
    @Transactional
    public void unfollowUser(SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionRepository.unfollowUser(subscriptionRequestDto.getFollowerId(), subscriptionRequestDto.getFolloweeId());
        subscriptionProducer.produce(subscriptionMapper.toEvent(subscriptionRequestDto, OperationType.DELETE));
    }
}

package OS.Yanwit.redis.cache.service.feed;

import OS.Yanwit.mapper.PostCacheMapper;
import OS.Yanwit.mapper.PostMapper;
import OS.Yanwit.model.dto.PostDto;
import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.model.entity.Post;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import OS.Yanwit.repository.PostRepository;
import OS.Yanwit.service.user.UserService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheServiceImpl implements FeedCacheService {

    @Value("${spring.data.redis.cache.settings.max-feed-size}")
    private long maxFeedSize;
    @Value("${spring.data.redis.lock-registry.lockSettings.feed.post-lock-key}")
    private String feedCacheKeyPrefix;
    @Value("${spring.data.redis.cache.settings.batch-size}")
    private int batchSize;

    private final ZSetOperations<String, Object> redisFeedZSetOps;
    private final ExpirableLockRegistry feedLockRegistry;
    private final PostCacheService postCacheService;
    private final PostCacheMapper postCacheMapper;
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostMapper postMapper;

    @Override
    public List<PostDto> getFeedByUserId(Long userId, Long postId){
        List<Long> followerPostIds = getFollowerPostIds(userId, postId);

        List<PostDto> postDtos = postCacheService.getPostCacheByIds(followerPostIds).stream()
                .map(postCacheMapper::toDto)
                .toList();

        if(postDtos.isEmpty()){
            UserDto userDto = userService.getUserById(userId);
            List<Post> posts;
            if(postId == null){
                posts = postRepository.findByAuthorsAndLimit(userDto.getFolloweesIds(), batchSize);
            } else {
                posts = postRepository.findByAuthorsAndLimitAndStartFromPostId(userDto.getFolloweesIds(), batchSize, postId);
            }
            postDtos = posts.stream()
                    .map(postMapper::toDto)
                    .toList();
        }
        return postDtos;
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void addPostIdToFollowerFeed(Long postId, Long subscriberId){
        String feedCacheKey = generateFeedCacheKey(subscriberId);
        long score = postId * (-1);

        lock(() ->{
            redisFeedZSetOps.add(feedCacheKey, postId, score);

            Long setSize = redisFeedZSetOps.zCard(feedCacheKey);
            if (setSize != null && setSize > maxFeedSize) {
                redisFeedZSetOps.removeRange(feedCacheKey, 0, setSize - maxFeedSize);
            }
        }, feedCacheKey);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void deletePostFromUserFeed(Long postId, Long subscriberId){
        String feedCacheKey = generateFeedCacheKey(subscriberId);
        long score = postId * (-1);

        lock(() -> {
            redisFeedZSetOps.remove(feedCacheKey, score);
        }, feedCacheKey);
    }

    private String generateFeedCacheKey(Long followerId) {
        return feedCacheKeyPrefix + followerId;
    }

    private List<Long> getFollowerPostIds(Long userId, Long postId) {
        String feedCacheKey = generateFeedCacheKey(userId);
        if (postId == null) {
            return getFeedInRange(feedCacheKey, 0, batchSize - 1);
        } else {
            Long rank = redisFeedZSetOps.rank(feedCacheKey, postId);

            if (rank == null) {
                return getFeedInRange(feedCacheKey, 0, batchSize - 1);
            }

            return getFeedInRange(feedCacheKey, rank + 1, rank + batchSize);
        }
    }

    private List<Long> getFeedInRange(String feedCacheKey, long rankStartPost, long rankEndPost) {
        Set<Object> result = redisFeedZSetOps.range(feedCacheKey, rankStartPost, rankEndPost);

        if (result == null || result.isEmpty()) {
            return Collections.emptyList();
        }

        return result.stream()
                .filter(Objects::nonNull)
                .map(obj -> {
                    if (obj instanceof Integer) {
                        return ((Integer) obj).longValue();
                    }
                    return (Long) obj;
                })
                .collect(Collectors.toList());
    }

    private void lock(Runnable operation, String lockKey) {

        Lock lock = feedLockRegistry.obtain(lockKey);

        if (lock.tryLock()) {
            try {
                operation.run();
            } finally {
                lock.unlock();
            }
        } else {
            throw new OptimisticLockException("Failed to obtain lock for key: " + lockKey);
        }
    }
}

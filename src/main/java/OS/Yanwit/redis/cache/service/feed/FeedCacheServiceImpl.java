package OS.Yanwit.redis.cache.service.feed;

import OS.Yanwit.mapper.PostCacheMapper;
import OS.Yanwit.mapper.PostMapper;
import OS.Yanwit.model.dto.PostDto;
import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.model.entity.Post;
import OS.Yanwit.property.RedisCacheProperty;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import OS.Yanwit.repository.PostRepository;
import OS.Yanwit.service.user.UserService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
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
    @Value("${spring.data.redis.cache.cache-settings.feed.name}")
    private String cacheKeyToFeedTtl;

    private final ZSetOperations<String, Object> redisFeedZSetOps;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ExpirableLockRegistry feedLockRegistry;
    private final RedisCacheProperty redisCacheProperty;
    private final PostCacheService postCacheService;
    private final PostCacheMapper postCacheMapper;
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostMapper postMapper;

    @Override
    public List<PostDto> getFeedByUserId(Long userId, Long postId){
        List<Long> postIds = getFollowerPostIds(userId, postId);

        Map<Long, PostDto> cachedPosts = postCacheService.getPostCacheByIds(postIds)
                .stream()
                .map(postCacheMapper::toDto)
                .collect(Collectors.toMap(PostDto::getId, Function.identity()));

        List<Long> missingIds = postIds.stream()
                .filter(id -> !cachedPosts.containsKey(id))
                .toList();

        if (!missingIds.isEmpty()) {
            List<PostDto> dbPosts = postRepository.findAllById(missingIds)
                    .stream()
                    .map(postMapper::toDto)
                    .toList();

            postCacheService.saveAll(dbPosts.stream().map(postCacheMapper::toPostCache).toList());

            dbPosts.forEach(post -> cachedPosts.put(post.getId(), post));
        }

        if(cachedPosts.isEmpty()){
            UserDto userDto = userService.getUserById(userId);
            List<Post> posts;
            if(postId == null){
                posts = postRepository.findByAuthorsAndLimit(userDto.getFolloweesIds(), batchSize);
            } else {
                posts = postRepository.findByAuthorsAndLimitAndStartFromPostId(userDto.getFolloweesIds(), batchSize, postId);
            }
            return posts.stream()
                    .map(postMapper::toDto)
                    .toList();
        }

        return postIds.stream()
                .filter(cachedPosts::containsKey)
                .map(cachedPosts::get)
                .collect(Collectors.toList());
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void addPostIdToFollowerFeed(Long postId, Long subscriberId){
        String feedCacheKey = generateFeedCacheKey(subscriberId);
        long score = postId * (-1);

        lock(() ->{
            redisFeedZSetOps.add(feedCacheKey, postId, score);
            long ttl = resolveTtl();
            redisTemplate.expire(feedCacheKey, ttl, TimeUnit.SECONDS);

            Long setSize = redisFeedZSetOps.zCard(feedCacheKey);
            if (setSize != null && setSize > maxFeedSize) {
                redisFeedZSetOps.removeRange(feedCacheKey, 0, setSize - maxFeedSize);
            }
        }, feedCacheKey);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void addPostIdToFollowerFeedByBatch(Collection<Long> postIds, Long subscriberId){
        String feedCacheKey = generateFeedCacheKey(subscriberId);

        lock(() -> {
                postIds.forEach(postId -> {

                    long score = postId * (-1);
                    redisFeedZSetOps.add(feedCacheKey, postId, score);
                    long ttl = resolveTtl();
                    redisTemplate.expire(feedCacheKey, ttl, TimeUnit.SECONDS);

                    Long setSize = redisFeedZSetOps.zCard(feedCacheKey);
                    if (setSize != null && setSize > maxFeedSize) {
                        redisFeedZSetOps.removeRange(feedCacheKey, 0, setSize - maxFeedSize);
                    }
                });
        }, feedCacheKey);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void deletePostFromUserFeed(Long postId, Long subscriberId){
        String feedCacheKey = generateFeedCacheKey(subscriberId);

        lock(() -> {
            long score = -1 * postId;
            redisFeedZSetOps.removeRangeByScore(feedCacheKey, score, score);
        }, feedCacheKey);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void deletePostsFromUserFeedByAuthor(Long userId, Long authorId){
        String feedCacheKey = generateFeedCacheKey(userId);
        long startPostId = getOldestPostIdInFeed(feedCacheKey);

        List<Post> postsToRemove = postRepository.findByAuthorFromPostIdWithLimit(authorId, startPostId, maxFeedSize);
        List<Long> postIds = postsToRemove.stream().map(Post::getId).toList();
        deletePostsFromUserFeedByBatch(postIds, userId);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void deletePostsFromUserFeedByBatch(Collection<Long> postIds, Long userId){
        String feedCacheKey = generateFeedCacheKey(userId);

        lock(() -> {
            postIds.forEach(postId -> {
                long score = -1 * postId;
                redisFeedZSetOps.removeRangeByScore(feedCacheKey, score, score);
            });
        }, feedCacheKey);
    }

    @Override
    @Retryable(retryFor = {OptimisticLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500, multiplier = 3))
    public void AddPostsFromAuthorToUserFeed(Long userId, Long authorId) {
        String feedCacheKey = generateFeedCacheKey(userId);
        long startPostId = getOldestPostIdInFeed(feedCacheKey);

        List<Post> postsToAdd = postRepository.findByAuthorFromPostIdWithLimit(authorId, startPostId, maxFeedSize);
        postCacheService.saveAll(postMapper.toListPostCache(postsToAdd));
        addPostIdToFollowerFeedByBatch(postsToAdd.stream().map(Post::getId).toList(), userId);
    }

    private long getOldestPostIdInFeed(String feedCacheKey) {
        Long size = redisFeedZSetOps.size(feedCacheKey);
        long startPostId = 0;
        if (size != null && size != 0) {
            Set<Object> lastPost = redisFeedZSetOps.range(feedCacheKey, -1, -1);
            startPostId = lastPost != null ? lastPost.stream()
                    .filter(Objects::nonNull)
                    .map(obj -> {
                        if (obj instanceof Integer) {
                            return ((Integer) obj).longValue();
                        }
                        return (Long) obj;
                    }).iterator().next() : 0;
        }
        return startPostId;
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

    private long resolveTtl() {
        return redisCacheProperty.getCacheSettings().containsKey(cacheKeyToFeedTtl) ?
                redisCacheProperty.getCacheSettings().get(cacheKeyToFeedTtl).getTtl() :
                redisCacheProperty.getDefaultTtl();
    }
}

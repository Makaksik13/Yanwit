package OS.Yanwit.redis.cache.service.post;

import OS.Yanwit.model.dto.CommentDto;
import OS.Yanwit.redis.cache.entity.PostCache;
import OS.Yanwit.redis.cache.repository.PostCacheRepository;
import OS.Yanwit.redis.cache.service.RedisOperations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {

    @Value("${spring.data.redis.cache.settings.max-post-comments-size}")
    private int maxCommentsQuantity;

    private final PostCacheRepository postCacheRepository;
    private final RedisOperations redisOperations;

    @Override
    @Async("postsCacheTaskExecutor")
    public void save(PostCache entity) {
        entity = redisOperations.updateOrSave(postCacheRepository, entity, entity.getId());

        log.info("Saved post with id {} to cache: {}", entity.getId(), entity);
    }

    @Override
    @Async("postsCacheTaskExecutor")
    public void saveAll(Collection<PostCache> entities) {
        redisOperations.saveAll(postCacheRepository, entities);
    }

    @Override
    public List<PostCache> getPostCacheByIds(List<Long> postIds){
        return postCacheRepository.findAllById(postIds);
    }

    @Override
    @Async("postsCacheTaskExecutor")
    public void deleteById(long postId) {

        PostCache post = redisOperations.findById(postCacheRepository, postId).orElse(null);

        log.info("Deleted post with id={} from cache", postId);

        if (post != null) {
            redisOperations.deleteById(postCacheRepository, postId);
        }
    }

    @Override
    @Async("postsCacheTaskExecutor")
    public void incrementLikes(long postId) {

        redisOperations.customUpdate(postCacheRepository, postId,  () -> {
            postCacheRepository.findById(postId).ifPresent(post ->{
                post.setLikesCount(post.getLikesCount() + 1);
                postCacheRepository.save(post);
            });
        });
    }

    @Override
    @Async("postsCacheTaskExecutor")
    public void addCommentToCachedPost(CommentDto commentDto) {
        redisOperations.customUpdate(postCacheRepository, commentDto.getPostId(), ()->{
            postCacheRepository.findById(commentDto.getPostId()).ifPresent(postCache -> {
                var comments = postCache.getComments();
                comments.add(commentDto);
                if(comments.size() > maxCommentsQuantity){
                    postCache.getComments().pollLast();
                }
                postCacheRepository.save(postCache);
            });
        });
    }

    @Override
    @Async("postsCacheTaskExecutor")
    public void deleteCommentFromCachedPost(CommentDto commentDto) {
        redisOperations.customUpdate(postCacheRepository, commentDto.getPostId(), ()->{
            postCacheRepository.findById(commentDto.getPostId()).ifPresent(postCache -> {
                var comments = postCache.getComments();
                comments.remove(commentDto);
                postCacheRepository.save(postCache);
            });
        });
    }
}

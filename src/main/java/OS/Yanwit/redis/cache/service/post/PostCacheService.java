package OS.Yanwit.redis.cache.service.post;

import OS.Yanwit.model.dto.CommentDto;
import OS.Yanwit.redis.cache.entity.PostCache;

import java.util.List;

public interface PostCacheService {

    void save(PostCache entity);

    void incrementLikes(long postId);

    void deleteById(long postId);

    List<PostCache> getPostCacheByIds(List<Long> postIds);

    void addCommentToCachedPost(Long postId, CommentDto commentDto);
}

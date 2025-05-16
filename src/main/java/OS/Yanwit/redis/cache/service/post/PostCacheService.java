package OS.Yanwit.redis.cache.service.post;

import OS.Yanwit.model.dto.CommentDto;
import OS.Yanwit.redis.cache.entity.PostCache;

import java.util.Collection;
import java.util.List;

public interface PostCacheService {

    void save(PostCache entity);

    void addNumberToLikesCountByPostId(long postId, long number);

    void deleteById(long postId);

    void saveAll(Collection<PostCache> entity);

    List<PostCache> getPostCacheByIds(List<Long> postIds);

    void addCommentToCachedPost(CommentDto commentDto);

    void deleteCommentFromCachedPost(CommentDto commentDto);
}

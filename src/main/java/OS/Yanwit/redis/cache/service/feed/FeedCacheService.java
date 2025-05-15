package OS.Yanwit.redis.cache.service.feed;

import OS.Yanwit.model.dto.PostDto;

import java.util.Collection;
import java.util.List;

public interface FeedCacheService {

    void addPostIdToFollowerFeed(Long postId, Long subscriberId);

    List<PostDto> getFeedByUserId(Long userId, Long postId);

    void addPostIdToFollowerFeedByBatch(Collection<Long> postIds, Long subscriberId);

    void deletePostFromUserFeed(Long postId, Long subscriberId);

    void AddPostsFromAuthorToUserFeed(Long userId, Long authorId);
}

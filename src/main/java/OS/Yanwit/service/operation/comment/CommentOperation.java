package OS.Yanwit.service.operation.comment;

import OS.Yanwit.kafka.event.comment.CommentEvent;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import OS.Yanwit.service.operation.Operation;

public interface CommentOperation extends Operation {
    void execute(PostCacheService postCacheService, CommentEvent event);
}

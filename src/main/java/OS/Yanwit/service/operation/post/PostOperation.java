package OS.Yanwit.service.operation.post;

import OS.Yanwit.kafka.event.post.PostEvent;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import OS.Yanwit.service.operation.Operation;

public interface PostOperation extends Operation {
    void execute(FeedCacheService feedCashService, PostEvent event);
}

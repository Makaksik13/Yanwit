package OS.Yanwit.service.operation;

import OS.Yanwit.kafka.event.post.PostEvent;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;

public interface PostOperation {
    void execute(FeedCacheService feedCashService, PostEvent event);
    OperationType getOperationType();
}

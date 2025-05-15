package OS.Yanwit.service.operation.subscription;

import OS.Yanwit.kafka.event.subscription.SubscriptionEvent;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import OS.Yanwit.service.operation.Operation;

public interface SubscriptionOperation extends Operation {
    void execute(FeedCacheService service, SubscriptionEvent event);
}

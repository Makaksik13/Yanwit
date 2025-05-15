package OS.Yanwit.service.operation.subscription;

import OS.Yanwit.kafka.event.subscription.SubscriptionEvent;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import org.springframework.stereotype.Component;

@Component
public class AddSubscriptionOperation implements SubscriptionOperation{

    @Override
    public void execute(FeedCacheService service, SubscriptionEvent event) {
        service.AddPostsFromAuthorToUserFeed(event.getFollowerId(), event.getFolloweeId());
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ADD;
    }
}

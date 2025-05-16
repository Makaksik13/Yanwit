package OS.Yanwit.service.operation.subscription;

import OS.Yanwit.kafka.event.subscription.SubscriptionEvent;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import OS.Yanwit.service.operation.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddSubscriptionOperation implements Operation<SubscriptionEvent> {
    private final FeedCacheService service;

    @Override
    public void execute(SubscriptionEvent event) {
        service.AddPostsFromAuthorToUserFeed(event.getFollowerId(), event.getFolloweeId());
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ADD_SUBSCRIPTION;
    }
}

package OS.Yanwit.service.operation.post;

import OS.Yanwit.kafka.event.post.PostEvent;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import org.springframework.stereotype.Component;

@Component
public class DeletePostOperation implements PostOperation {

    @Override
    public void execute(FeedCacheService feedCashService, PostEvent event) {
        event.getFollowersIds()
                .forEach(followerId ->{
                    feedCashService.deletePostFromUserFeed(event.getPostId(), followerId);
                });
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.DELETE;
    }
}

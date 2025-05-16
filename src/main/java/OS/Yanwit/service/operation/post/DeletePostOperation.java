package OS.Yanwit.service.operation.post;

import OS.Yanwit.kafka.event.post.PostEvent;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import OS.Yanwit.service.operation.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletePostOperation implements Operation<PostEvent> {
    private final FeedCacheService feedCashService;

    @Override
    public void execute(PostEvent event) {
        event.getFollowersIds()
                .forEach(followerId ->{
                    feedCashService.deletePostFromUserFeed(event.getPostId(), followerId);
                });
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.DELETE_POST;
    }
}

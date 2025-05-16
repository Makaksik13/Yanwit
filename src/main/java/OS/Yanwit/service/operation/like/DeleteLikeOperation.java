package OS.Yanwit.service.operation.like;

import OS.Yanwit.kafka.event.like.PostLikeEvent;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import OS.Yanwit.service.operation.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteLikeOperation implements Operation<PostLikeEvent> {

    private final PostCacheService postCacheService;

    @Override
    public OperationType getOperationType() {
        return OperationType.DELETE_LIKE;
    }

    @Override
    public void execute(PostLikeEvent event) {
        postCacheService.addNumberToLikesCountByPostId(event.getPostId(), -1);
    }
}

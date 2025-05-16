package OS.Yanwit.service.operation.comment;

import OS.Yanwit.kafka.event.comment.CommentEvent;
import OS.Yanwit.mapper.CommentMapper;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import OS.Yanwit.service.operation.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddCommentOperation implements Operation<CommentEvent> {
    private final PostCacheService postCacheService;
    private final CommentMapper commentMapper;
    @Override
    public void execute(CommentEvent event) {
        postCacheService.addCommentToCachedPost(commentMapper.toDto(event));
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.ADD_COMMENT;
    }
}

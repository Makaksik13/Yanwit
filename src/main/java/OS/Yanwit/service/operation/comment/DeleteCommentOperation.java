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
public class DeleteCommentOperation implements Operation<CommentEvent> {

    private final CommentMapper commentMapper;
    private final PostCacheService postCacheService;

    @Override
    public void execute(CommentEvent event) {
        postCacheService.deleteCommentFromCachedPost(commentMapper.toDto(event));
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.DELETE_COMMENT;
    }
}

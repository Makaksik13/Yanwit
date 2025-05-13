package OS.Yanwit.service.operation.comment;

import OS.Yanwit.kafka.event.comment.CommentEvent;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.model.dto.CommentDto;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import org.springframework.stereotype.Component;

@Component
public class DeleteCommentOperation implements CommentOperation{

    @Override
    public void execute(PostCacheService postCacheService, CommentEvent event) {
        postCacheService.deleteCommentFromCachedPost(CommentDto.builder()
                .id(event.getId())
                .authorId(event.getUserId())
                .content(event.getContent())
                .postId(event.getPostId())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build()
        );
        //todo упростить создание дто, м.б. через маппер
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.DELETE;
    }
}

package OS.Yanwit.service.comment;

import OS.Yanwit.model.dto.CommentDto;
import OS.Yanwit.model.dto.CommentToCreateDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService {
    CommentDto createComment(long postId, long userId, CommentToCreateDto commentDto);

    CommentDto deleteComment(long commentId);

    @Transactional(readOnly = true)
    List<CommentDto> getAllPostComments(long postId);
}

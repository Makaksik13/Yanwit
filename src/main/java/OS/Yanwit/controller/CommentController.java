package OS.Yanwit.controller;

import OS.Yanwit.model.dto.CommentDto;
import OS.Yanwit.model.dto.CommentToCreateDto;
import OS.Yanwit.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name="Контроллер комментариев")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Создание комментария на посте с идентификатором postId от пользователя с идентификатором userId")
    @PostMapping("/post/{postId}/user/{userId}")
    public CommentDto createComment(@PathVariable("postId") long postId,
                                    @PathVariable("userId") long userId,
                                    @RequestBody @Valid CommentToCreateDto commentDto) {
        return commentService.createComment(postId, userId, commentDto);
    }

    @Operation(summary = "Получение всех комментариев поста с идентификатором postId")
    @GetMapping("/post/{postId}")
    public List<CommentDto> getAllPostComments(@PathVariable("postId") long postId) {
        return commentService.getAllPostComments(postId);
    }

    @Operation(summary = "Удаление комментария с идентификатором commentId")
    @DeleteMapping("/{commentId}")
    public CommentDto deleteComment(@PathVariable("commentId") long commentId) {
        return commentService.deleteComment(commentId);
    }
}

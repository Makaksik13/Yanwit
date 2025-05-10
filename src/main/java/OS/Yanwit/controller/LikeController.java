package OS.Yanwit.controller;

import OS.Yanwit.model.dto.LikeDto;
import OS.Yanwit.service.like.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Контроллер лайков")
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @Operation(summary = "Создание лайка на посте с идентификатором postId от пользователя с идентификатором userId")
    @PostMapping("/post/{postId}/user/{userId}")
    public LikeDto likePost(@PathVariable long postId,
                            @PathVariable long userId) {
        return likeService.addLikeOnPost(userId, postId);
    }

    @Operation(summary = "Удаление лайка с идентификатором likeId на посте с идентификатором postId от пользователя с идентификатором userId")
    @DeleteMapping("/post/{postId}/like/{likeId}/user/{userId}")
    public void deleteLikeFromPost(@PathVariable long postId,
                                   @PathVariable long likeId,
                                   @PathVariable long userId) {
        likeService.removeLikeFromPost(likeId, userId, postId);
    }
}

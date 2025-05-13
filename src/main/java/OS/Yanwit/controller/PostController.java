package OS.Yanwit.controller;

import OS.Yanwit.model.dto.PostCreateDto;
import OS.Yanwit.model.dto.PostDto;
import OS.Yanwit.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Контроллер постов")
@RestController
@RequestMapping("posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("{postId}")
    @Operation(summary = "Получение поста по его идентификатору")
    public PostDto findById(@PathVariable @Parameter Long postId) {
        return postService.findById(postId);
    }

    @PutMapping("{postId}")
    @Operation(summary = "Обновление поста по его идентификатору")
    public PostDto update(@PathVariable @Parameter Long postId, @RequestBody String content){
        return postService.update(postId, content);
    }

    @PostMapping
    @Operation(summary = "Создание поста")
    public PostDto create(@RequestBody @Valid PostCreateDto postCreateDto) {
        return postService.create(postCreateDto);
    }

    @PostMapping("publication/{postId}")
    @Operation(summary = "Публикация поста по его идентификатору")
    public PostDto publish(@PathVariable @Parameter Long postId) {
        return postService.publish(postId);
    }

    @DeleteMapping("{postId}")
    @Operation(summary = "Удаление поста по его идентификатору")
    public void deleteById(@PathVariable @Parameter Long postId) {
        postService.deleteById(postId);
    }
}

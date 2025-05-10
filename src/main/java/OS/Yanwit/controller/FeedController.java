package OS.Yanwit.controller;

import OS.Yanwit.model.dto.PostDto;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import OS.Yanwit.service.feed_heater.FeedHeaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name="Контроллер ленты новостей")
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedCacheService feedCacheService;
    private final FeedHeaterService feedHeaterService;

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Получение ленты новостей для пользователя с идентификатором userId")
    public List<PostDto> getUserFeed(@RequestParam(value = "postId", required = false) Long postId,
                                    @Parameter @PathVariable Long userId) {
        return feedCacheService.getFeedByUserId(userId, postId);
    }

    @PostMapping("/heat")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Прогрев кеша ленты новостей")
    public void sendHeatEventsAsync() {
        feedHeaterService.heatUp();
    }
}

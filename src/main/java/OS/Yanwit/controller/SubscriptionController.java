package OS.Yanwit.controller;

import OS.Yanwit.model.dto.SubscriptionRequestDto;
import OS.Yanwit.service.subscription.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("subscriptions")
@Tag(name = "Контроллер подписок")
@Validated
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Подписаться на пользователя")
    @PostMapping("following")
    public void followUser(@Valid @RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionService.followUser(subscriptionRequestDto);
    }

    @Operation(summary = "Отписаться от пользователя")
    @PostMapping("unfollowing")
    public void unfollowUser(@Valid @RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionService.unfollowUser(subscriptionRequestDto);
    }
}


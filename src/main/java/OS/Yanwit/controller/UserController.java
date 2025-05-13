package OS.Yanwit.controller;

import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name="Контроллер пользователей")
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;

    @Operation(summary = "Создание пользователя")
    @PostMapping("creature")
    public UserDto createUser(@RequestBody @Valid UserDto userDto){
        return userService.createUser(userDto);
    }

    @GetMapping("{userId}")
    @Operation(summary = "Получение пользователя по его идентификатору")
    public UserDto getUserById(@PathVariable("userId") long userId) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("{userId}")
    @Operation(summary = "Удаление пользователя по его идентификатору")
    public void deleteUserById(@PathVariable("userId") long userId){
        userService.deleteUserById(userId);
    }
}

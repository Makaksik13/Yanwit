package OS.Yanwit.service.user;

import OS.Yanwit.model.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    @Transactional
    UserDto createUser(@Valid UserDto userDto);

    @Transactional
    UserDto getUserById(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);

    @Transactional(readOnly = true)
    List<UserDto> getAllUsers();

    @Transactional
    void deleteUserById(long userId);
}

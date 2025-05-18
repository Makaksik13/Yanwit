package OS.Yanwit.service.user;

import OS.Yanwit.mapper.UserMapper;
import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.model.entity.User;
import OS.Yanwit.repository.UserRepository;
import OS.Yanwit.service.common_methods.CommonServiceMethods;
import com.google.common.hash.Hashing;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CommonServiceMethods commonServiceMethods;

    @Override
    @Transactional
    public UserDto createUser(@Valid UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setPassword(Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString());
        User saved = userRepository.save(user);
        log.info("Created new user {}", saved.getId());
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserDto getUserById(long userId) {
        User user = commonServiceMethods.findEntityById(userRepository, userId, "User");
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers(){
        List<User> users = userRepository.findAll();
        return userMapper.toListDto(users);
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }
    //todo добавь логирование для каждого метода
}

package OS.Yanwit.service.user;

import OS.Yanwit.exception.NotFoundException;
import OS.Yanwit.mapper.UserMapper;
import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.model.entity.User;
import OS.Yanwit.repository.UserRepository;
import OS.Yanwit.service.common_methods.CommonServiceMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Тестирование UserService")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private CommonServiceMethods commonServiceMethods;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User expectedUser;
    private UserDto expectedUserDto;

    private User createUserWithId(long id){
        return User.builder().id(id).build();
    }

    @BeforeEach
    public void init() {
        var followers = List.of(
                createUserWithId(1),
                createUserWithId(2)
        );
        var followersIds = List.of(1L, 2L);

        var followees = List.of(
                createUserWithId(3),
                createUserWithId(4)
        );
        var followeesIds = List.of(3L, 4L);

        expectedUser = User.builder().id(1L)
                .username("Иван")
                .email("test@email.com")
                .phone("7777777")
                .password("password")
                .aboutMe("test about me")
                .followers(followers)
                .followees(followees)
                .build();

        expectedUserDto = UserDto.builder()
                .id(1L)
                .username("Иван")
                .email("test@email.com")
                .phone("7777777")
                .password("password")
                .aboutMe("test about me")
                .followersIds(followersIds)
                .followeesIds(followeesIds)
                .build();
    }


    @Nested
    class GetDesign {

        @Test
        @DisplayName("Попытка получить существующего пользователя")
        public void testGetByIdWithSuccessfulGetting() {
            when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.of(expectedUser));

            UserDto actualPatientDto = userService.getUserById(expectedUser.getId());

            verify(commonServiceMethods, times(1))
                    .findEntityById(userRepository, expectedUser.getId(), "User");
            verify(userRepository, times(1)).findById(expectedUser.getId());
            verify(userMapper, times(1)).toDto(expectedUser);
            assertThat(actualPatientDto).isEqualTo(expectedUserDto);
        }

        @Test
        @DisplayName("Попытка получить несуществующего пользователя")
        public void testGetByIdWithNotFoundException() {
            when(userRepository.findById(expectedUser.getId())).thenReturn(Optional.empty());

            Throwable exception = catchThrowable(() -> userService.getUserById(expectedUser.getId()));

            assertThat(exception)
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage(String.format("%s with id %d not found", "User", expectedUser.getId()));
        }
    }
}

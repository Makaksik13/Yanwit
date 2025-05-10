package OS.Yanwit.redis.cache.service.author;

import OS.Yanwit.mapper.AuthorMapper;
import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.redis.cache.entity.AuthorCache;
import OS.Yanwit.redis.cache.repository.AuthorCacheRepository;
import OS.Yanwit.redis.cache.service.RedisOperations;
import OS.Yanwit.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Slf4j
@Service
@RequiredArgsConstructor
@Async("authorsCacheTaskExecutor")
public class AuthorCacheServiceImpl implements AuthorCacheService {

    private final AuthorCacheRepository authorCacheRepository;
    private final RedisOperations redisOperations;
    private final UserService userService;
    private final AuthorMapper authorMapper;

    @Override
    public CompletableFuture<UserDto> save(long id) {

        UserDto userDto = userService.getUserById(id);
        AuthorCache redisUser = authorMapper.toCache(userDto);

        AuthorCache entity = redisOperations.updateOrSave(authorCacheRepository, redisUser, redisUser.getId());

        log.info("Saved author with id {} to cache: {}", entity.getId(), redisUser);

        return completedFuture(userDto);
    }

    @Override
    public CompletableFuture<UserDto> getUserDtoById(long id) {

        UserDto userDto = userService.getUserById(id);

        return completedFuture(userDto);
    }
}


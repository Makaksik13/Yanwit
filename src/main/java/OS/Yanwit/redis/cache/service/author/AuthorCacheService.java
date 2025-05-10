package OS.Yanwit.redis.cache.service.author;

import OS.Yanwit.model.dto.UserDto;

import java.util.concurrent.CompletableFuture;

public interface AuthorCacheService {

    CompletableFuture<UserDto> save(long id);

    CompletableFuture<UserDto> getUserDtoById(long id);
}

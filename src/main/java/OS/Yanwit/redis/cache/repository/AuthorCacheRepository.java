package OS.Yanwit.redis.cache.repository;

import OS.Yanwit.redis.cache.entity.AuthorCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface AuthorCacheRepository extends KeyValueRepository<AuthorCache, Long> {
}

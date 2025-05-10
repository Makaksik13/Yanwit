package OS.Yanwit.redis.cache.repository;

import OS.Yanwit.redis.cache.entity.PostCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface PostCacheRepository extends KeyValueRepository<PostCache, Long> {
}

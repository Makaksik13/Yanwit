package OS.Yanwit.mapper;

import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.redis.cache.entity.AuthorCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    AuthorCache toCache(UserDto userDto);
}

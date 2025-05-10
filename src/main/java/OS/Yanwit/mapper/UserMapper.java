package OS.Yanwit.mapper;

import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "followersIds", source = "followers", qualifiedByName = "getIdsFromUsers")
    @Mapping(target = "followeesIds",source = "followees", qualifiedByName = "getIdsFromUsers")
    UserDto toDto(User user);

    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followees", ignore = true)
    User toEntity(UserDto userDto);

    List<UserDto> toListDto(List<User> users);

    @Named("getIdsFromUsers")
    default List<Long> getIdsFromUsers(List<User> users) {
        if(users == null) return null;
        return users.stream().map(User::getId).toList();
    }
}

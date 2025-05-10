package OS.Yanwit.mapper;

import OS.Yanwit.model.dto.LikeDto;
import OS.Yanwit.model.entity.Like;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LikeMapper {

    @Mapping(source = "postId", target = "post.id")
    Like toEntity(LikeDto likeDto);

    @Mapping(source = "post.id", target = "postId")
    LikeDto toDto(Like like);
}

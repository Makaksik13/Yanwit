package OS.Yanwit.mapper;

import OS.Yanwit.model.dto.PostCreateDto;
import OS.Yanwit.model.dto.PostDto;
import OS.Yanwit.model.entity.Like;
import OS.Yanwit.model.entity.Post;
import OS.Yanwit.redis.cache.entity.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    PostDto toDto(Post post);

    Post toEntity(PostCreateDto postCreateDto);

    @Mapping(source = "likes", target = "likesCount", qualifiedByName = "getCountFromLikeList")
    PostCache toPostCache(Post post);

    PostCache toPostCache(PostDto post);

    List<PostDto> toListDto(List<Post> posts);

    List<PostCache> toListPostCacheFromDto(List<PostDto> posts);

    List<PostCache> toListPostCache(List<Post> posts);

    @Named("getCountFromLikeList")
    default int getCountFromLikeList(List<Like> likes) {
        return likes != null ? likes.size() : 0;
    }
}

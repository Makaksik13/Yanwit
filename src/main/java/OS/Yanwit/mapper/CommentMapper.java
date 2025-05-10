package OS.Yanwit.mapper;

import OS.Yanwit.kafka.event.comment.CommentEvent;
import OS.Yanwit.model.dto.CommentDto;
import OS.Yanwit.model.dto.CommentToCreateDto;
import OS.Yanwit.model.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto toDto(Comment comment);

    @Mapping(source = "userId", target = "authorId")
    CommentDto toDto(CommentEvent commentEvent);

    @Mapping(source = "postId", target = "post.id")
    Comment toEntity(CommentToCreateDto commentDto);

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "authorId", target = "userId")
    CommentEvent toEvent(Comment comment);
}

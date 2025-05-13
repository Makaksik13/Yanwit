package OS.Yanwit.service.comment;

import OS.Yanwit.kafka.event.comment.CommentEvent;
import OS.Yanwit.kafka.producer.comment.CommentProducer;
import OS.Yanwit.mapper.CommentMapper;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.model.dto.CommentDto;
import OS.Yanwit.model.dto.CommentToCreateDto;
import OS.Yanwit.model.entity.Comment;
import OS.Yanwit.model.entity.Post;
import OS.Yanwit.redis.cache.service.author.AuthorCacheService;
import OS.Yanwit.repository.CommentRepository;
import OS.Yanwit.repository.PostRepository;
import OS.Yanwit.service.CommonMethods.CommonServiceMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommonServiceMethods commonServiceMethods;
    private final PostRepository postRepository;
    private final AuthorCacheService authorCacheService;
    private final CommentProducer commentProducer;

    private void generateAndSendCommentEventToKafka(Comment comment, OperationType operationType){
        CommentEvent event = commentMapper.toEvent(comment);
        event.setOperationType(operationType);
        commentProducer.produce(event);
    }

    @Override
    public CommentDto createComment(long postId, long userId, CommentToCreateDto commentDto) {

        Post post = commonServiceMethods.findEntityById(postRepository, postId, "Post");
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userId);
        comment.setPostId(post.getId());

        Comment savedComment = commentRepository.save(comment);
        authorCacheService.save(userId);

        generateAndSendCommentEventToKafka(savedComment, OperationType.ADD);
        log.info("Created comment on post {} authored by {} with id {}", postId, userId, savedComment.getId());
        return commentMapper.toDto(savedComment);
    }

    @Override
    public CommentDto deleteComment(long commentId) {

        Comment comment = commonServiceMethods.findEntityById(commentRepository, commentId, "Comment");
        CommentDto commentToDelete = commentMapper.toDto(comment);

        commentRepository.deleteById(commentId);
        generateAndSendCommentEventToKafka(comment, OperationType.DELETE);
        log.info("Deleted comment {} on post {}", commentId, comment.getPostId());
        return commentToDelete;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllPostComments(long postId) {

        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
}

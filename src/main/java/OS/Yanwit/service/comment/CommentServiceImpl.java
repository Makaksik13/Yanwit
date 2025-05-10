package OS.Yanwit.service.comment;

import OS.Yanwit.kafka.event.comment.CommentEvent;
import OS.Yanwit.kafka.producer.comment.CommentProducer;
import OS.Yanwit.mapper.CommentMapper;
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

    private void generateAndSendCommentEventToKafka(Comment comment){
        CommentEvent event = commentMapper.toEvent(comment);
        commentProducer.produce(event);
    }

    @Override
    public CommentDto createComment(long postId, long userId, CommentToCreateDto commentDto) {

        Post post = commonServiceMethods.findEntityById(postRepository, postId, "Post");
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userId);
        comment.setPost(post);

        commentRepository.save(comment);
        authorCacheService.save(userId);

        generateAndSendCommentEventToKafka(comment);
        log.info("Created comment on post {} authored by {}", postId, userId);
        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDto deleteComment(long commentId) {

        Comment comment = commonServiceMethods.findEntityById(commentRepository, commentId, "Comment");
        CommentDto commentToDelete = commentMapper.toDto(comment);

        commentRepository.deleteById(commentId);
        log.info("Deleted comment {} on post {}", commentId, comment.getPost().getId());
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

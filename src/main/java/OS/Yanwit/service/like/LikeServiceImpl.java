package OS.Yanwit.service.like;

import OS.Yanwit.exception.NotFoundException;
import OS.Yanwit.exception.RepeatLikeCreationException;
import OS.Yanwit.kafka.event.like.PostLikeEvent;
import OS.Yanwit.kafka.producer.like.PostLikeProducer;
import OS.Yanwit.mapper.LikeMapper;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.model.dto.LikeDto;
import OS.Yanwit.model.entity.Like;
import OS.Yanwit.model.entity.Post;
import OS.Yanwit.repository.LikeRepository;
import OS.Yanwit.repository.PostRepository;
import OS.Yanwit.service.CommonMethods.CommonServiceMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostRepository postRepository;
    private final CommonServiceMethods commonServiceMethods;
    private final PostLikeProducer postLikeProducer;

    private void generateAndSendLikeEventToKafka(Like like, Long postId, OperationType operationType){
        PostLikeEvent event = PostLikeEvent.builder()
                .postId(postId)
                .authorId(like.getUserId())
                .likeId(like.getId())
                .build();
        event.setOperationType(operationType);
        postLikeProducer.produce(event);
    }

    @Override
    @Transactional
    public LikeDto addLikeOnPost(long userId, long postId) {
        LikeDto likeDto = createLikeDto(null, userId, dto -> dto.setPostId(postId));

        if(!existsLikeOnPostFromUser(postId, userId)){
            throw new RepeatLikeCreationException(String.format("Repeated attempt to create a like " +
                    "on the post with id = %d by a user with id = %d", postId, userId));
        }

        Post post = commonServiceMethods.findEntityById(postRepository, postId, "Post");

        Like like = likeMapper.toEntity(likeDto);
        post.getLikes().add(like);
        like = likeRepository.save(like);

        generateAndSendLikeEventToKafka(like, postId, OperationType.ADD_LIKE);

        log.info("Like with likeId = {} was added on post with postId = {} by user with userId = {}", like.getId(), postId, userId);

        return likeMapper.toDto(like);
    }

    @Override
    @Transactional
    public void removeLikeFromPostByUserIdAndPostId(long userId, long postId) {
        Like like = likeRepository.findByUserIdAndPostId(userId, postId);
        if(like == null){
            throw new NotFoundException(
                    String.format("There is no like from the user with id = %d on the post with id = %d", userId, postId)
            );
        }
        generateAndSendLikeEventToKafka(like, postId, OperationType.DELETE_LIKE);
        likeRepository.delete(like);
        log.info("Like with likeId = {} was removed", like.getId());
    }

    private boolean existsLikeOnPostFromUser(long postId, long userId){
        return likeRepository.findByUserIdAndPostId(userId, postId) != null;
    }

    private LikeDto createLikeDto(Long id, Long userId, Consumer<LikeDto> function) {
        LikeDto likeDto = new LikeDto();
        likeDto.setId(id);
        likeDto.setUserId(userId);
        function.accept(likeDto);
        return likeDto;
    }
}

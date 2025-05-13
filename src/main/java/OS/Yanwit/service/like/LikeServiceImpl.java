package OS.Yanwit.service.like;

import OS.Yanwit.kafka.event.like.PostLikeEvent;
import OS.Yanwit.kafka.producer.like.PostLikeProducer;
import OS.Yanwit.mapper.LikeMapper;
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

    private void generateAndSendLikeEventToKafka(Like like, Post post){
        PostLikeEvent event = PostLikeEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .userId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .build();
        postLikeProducer.produce(event);
    }

    @Override
    @Transactional
    public LikeDto addLikeOnPost(long userId, long postId) {
        LikeDto likeDto = createLikeDto(null, userId, dto -> dto.setPostId(postId));

        Post post = commonServiceMethods.findEntityById(postRepository, postId, "Post");

        Like like = likeMapper.toEntity(likeDto);
        post.getLikes().add(like);
        like = likeRepository.save(like);

        generateAndSendLikeEventToKafka(like, post);

        log.info("Like with likeId = {} was added on post with postId = {} by user with userId = {}", like.getId(), postId, userId);

        return likeMapper.toDto(like);
    }

    @Override
    @Transactional
    public void removeLikeFromPost(long likeId) {
        likeRepository.deleteById(likeId);
        log.info("Like with likeId = {} was removed", likeId);
    }

    private LikeDto createLikeDto(Long id, Long userId, Consumer<LikeDto> function) {
        LikeDto likeDto = new LikeDto();
        likeDto.setId(id);
        likeDto.setUserId(userId);
        function.accept(likeDto);
        return likeDto;
    }
}

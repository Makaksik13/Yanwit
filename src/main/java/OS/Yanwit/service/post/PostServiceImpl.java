package OS.Yanwit.service.post;

import OS.Yanwit.exception.NotFoundException;
import OS.Yanwit.kafka.event.post.PostEvent;
import OS.Yanwit.kafka.producer.split.MessageSplitPostProducer;
import OS.Yanwit.mapper.PostMapper;
import OS.Yanwit.model.OperationType;
import OS.Yanwit.model.dto.PostCreateDto;
import OS.Yanwit.model.dto.PostDto;
import OS.Yanwit.model.dto.UserDto;
import OS.Yanwit.model.entity.Post;
import OS.Yanwit.redis.cache.service.author.AuthorCacheService;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import OS.Yanwit.repository.PostRepository;
import OS.Yanwit.service.common_methods.CommonServiceMethods;
import OS.Yanwit.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final CommonServiceMethods commonServiceMethods;
    private final AuthorCacheService authorCacheService;
    private final PostCacheService postCacheService;
    private final MessageSplitPostProducer messageSplitPostProducer;
    private final UserService userService;

    private void generateAndSendPostEventToKafka(Post post, OperationType operationType){
        UserDto author = userService.getUserById(post.getAuthorId());
        PostEvent event = PostEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .followersIds(author.getFollowersIds())
                .publishedAt(post.getPublishedAt())
                .build();
        event.setOperationType(operationType);

        messageSplitPostProducer.produce(event);
    }

    @Override
    public PostDto findById(Long id) {
        Post post = commonServiceMethods.findEntityById(postRepository, id, "Post");
        if(post.isDeleted()) {
            throw new NotFoundException(String.format("Post with id %d not found", id));
        }
        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto update(Long id, String content){
        Post post = commonServiceMethods.findEntityById(postRepository, id, "Post");
        post.setContent(content);
        Post updatedPost = postRepository.save(post);

        postCacheService.save(postMapper.toPostCache(post));

        return postMapper.toDto(updatedPost);
    }

    @Override
    public List<PostDto> findLatestByAuthor(Long authorId, long limit) {
        List<Post> posts = postRepository.findLatestByAuthor(authorId, limit);
        return postMapper.toListDto(posts);
    }

    @Override
    @Transactional
    public PostDto create(PostCreateDto postCreateDto) {
        Post post = postRepository.save(postMapper.toEntity(postCreateDto));
        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto publish(Long id) {
        Post post = commonServiceMethods.findEntityById(postRepository, id, "Post");
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);

        authorCacheService.save(post.getAuthorId());
        postCacheService.save(postMapper.toPostCache(post));

        generateAndSendPostEventToKafka(post, OperationType.ADD_POST);

        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Post post = commonServiceMethods.findEntityById(postRepository, id, "Post");

        postCacheService.deleteById(post.getId());
        postRepository.deleteById(id);
        generateAndSendPostEventToKafka(post, OperationType.DELETE_POST);
    }
}

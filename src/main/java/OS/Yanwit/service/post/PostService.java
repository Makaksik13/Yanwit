package OS.Yanwit.service.post;

import OS.Yanwit.model.dto.PostCreateDto;
import OS.Yanwit.model.dto.PostDto;
import org.springframework.transaction.annotation.Transactional;

public interface PostService {
    PostDto findById(Long id);

    @Transactional
    PostDto create(PostCreateDto postCreateDto);

    @Transactional
    PostDto publish(Long id);

    @Transactional
    void deleteById(Long id);
}

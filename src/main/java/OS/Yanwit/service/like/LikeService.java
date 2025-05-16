package OS.Yanwit.service.like;

import OS.Yanwit.model.dto.LikeDto;
import org.springframework.transaction.annotation.Transactional;

public interface LikeService {
    @Transactional
    LikeDto addLikeOnPost(long userId, long postId);

    @Transactional
    void removeLikeFromPostByUserIdAndPostId(long userId, long postI);
}

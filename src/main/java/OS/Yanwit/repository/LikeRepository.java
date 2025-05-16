package OS.Yanwit.repository;

import OS.Yanwit.model.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT l FROM Like l " +
            "WHERE l.userId = :authorId AND " +
            "l.post.id = :postId")
    Like findByUserIdAndPostId(@Param("authorId") long authorId,
                               @Param("postId") long postId);
}

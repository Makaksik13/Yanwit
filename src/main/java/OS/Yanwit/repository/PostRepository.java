package OS.Yanwit.repository;

import OS.Yanwit.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p " +
            "WHERE p.published = true AND " +
            "p.deleted = false AND " +
            "p.authorId IN :authors AND " +
            "p.id >= :postId " +
            "ORDER BY p.id DESC " +
            "LIMIT :countPosts")
    List<Post> findByAuthorsAndLimitAndStartFromPostId(@Param("authors") List<Long> authors,
                                                       @Param("countPosts") int countPosts,
                                                       @Param("postId")long postId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published = true AND " +
            "p.deleted = false AND " +
            "p.authorId IN :authors " +
            "ORDER BY p.id DESC " +
            "LIMIT :countPosts")
    List<Post> findByAuthorsAndLimit(@Param("authors") List<Long> authors, @Param("countPosts") int countPosts);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published = true AND " +
            "p.deleted = false AND " +
            "p.authorId = :authorId " +
            "ORDER BY p.id DESC " +
            "LIMIT :limit")
    List<Post> findLatestByAuthor(@Param("authorId") long authorId, @Param("limit") long limit);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published = true AND " +
            "p.deleted = false AND " +
            "p.authorId = :authorId AND " +
            "p.id >= :startPostId " +
            "ORDER BY p.id DESC " +
            "LIMIT :limit")
    List<Post> findByAuthorFromPostIdWithLimit(@Param("authorId") long authorId,
                                      @Param("startPostId") long startPostId,
                                      @Param("limit") long limit);
}

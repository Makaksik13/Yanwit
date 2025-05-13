package OS.Yanwit.redis.cache.entity;

import OS.Yanwit.model.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.NavigableSet;
import java.util.TreeSet;

@Getter
@Setter
@ToString(exclude = {"comments"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("posts")
public class PostCache implements Serializable {

    @Id
    private long id;
    private NavigableSet<CommentDto> comments = new TreeSet<>();
    private long authorId;
    private String content;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private long likesCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCache that = (PostCache) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}


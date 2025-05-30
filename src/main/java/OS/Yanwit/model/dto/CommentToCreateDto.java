package OS.Yanwit.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentToCreateDto {
    @NotNull(message = "Content cannot be null")
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 4096, message = "Content cannot exceed 4096 characters")
    private String content;
    private long postId;
}

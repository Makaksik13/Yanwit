package OS.Yanwit.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "Violation", description = "Нарушение валидации")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Violation {
    @Schema(description = "Название поля", example = "email")
    private String fieldName;

    @Schema(description = "Сообщение об ошибке", example = "Email должен быть валидным")
    private String message;
}

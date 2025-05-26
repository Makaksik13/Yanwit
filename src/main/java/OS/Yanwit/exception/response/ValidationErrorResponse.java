package OS.Yanwit.exception.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(name = "ValidationErrorResponse", description = "Ответ с ошибками валидации")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ValidationErrorResponse {
    @Schema(description = "Список нарушений валидации")
    private List<Violation> violations;
}

package OS.Yanwit.exception.responce;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Jacksonized
@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {

    private final List<Violation> violations;
}

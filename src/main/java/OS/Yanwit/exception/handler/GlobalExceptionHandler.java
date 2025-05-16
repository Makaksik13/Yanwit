package OS.Yanwit.exception.handler;

import OS.Yanwit.exception.NotFoundException;
import OS.Yanwit.exception.RepeatLikeCreationException;
import OS.Yanwit.exception.responce.ErrorResponse;
import OS.Yanwit.exception.responce.ValidationErrorResponse;
import OS.Yanwit.exception.responce.Violation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());
        log.error("Validation exception", e);
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        log.error("Validation exception", e);
        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(RepeatLikeCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRepeatLikeCreationException(RepeatLikeCreationException e, HttpServletRequest request) {
        log.error("Not found exception", e);
        return buildErrorResponse(e, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        log.error("Not found exception", e);
        return buildErrorResponse(e, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("Runtime exception: {}", e.getMessage(), e);
        return buildErrorResponse(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse buildErrorResponse(Exception e, HttpServletRequest request, HttpStatus status) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(request.getRequestURI())
                .status(status)
                .message(e.getMessage())
                .build();
    }

}

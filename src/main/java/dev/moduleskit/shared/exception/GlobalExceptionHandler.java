package dev.moduleskit.shared.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // 1) DTO validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @SuppressWarnings("null")
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, List<String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                fe -> messageSource.getMessage((org.springframework.context.support.DefaultMessageSourceResolvable) fe, LocaleContextHolder.getLocale()),
                                Collectors.toList())));

        logger.warn("Validation error on {}: {}", request.getDescription(false), errors);
        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponse(400, errors));
    }

    // 2) All business/domain exceptions (BaseException)
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ValidationErrorResponse> handleBaseException(BaseException ex, WebRequest request) {
        Map<String, List<String>> errors = Map.of(
                "general_error", List.of(ex.getMessage()));

        logger.warn("Business exception on {}: {}", request.getDescription(false), ex.getMessage());
        return ResponseEntity.status(ex.getStatus().value())
                .body(new ValidationErrorResponse(ex.getStatus().value(), errors));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ValidationErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        logger.warn("Access denied on {}", request.getDescription(false));
        Map<String, List<String>> errors = Map.of(
                "access_error", List.of("You do not have permission to access this resource"));
        return ResponseEntity.status(403).body(new ValidationErrorResponse(403, errors));
    }

    // 3) Unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleGeneric(Exception ex, WebRequest request) {
        Map<String, List<String>> errors = Map.of(
                "general_error", List.of("Unexpected error")
        );

        logger.error("Unexpected error on {}: ", request.getDescription(false), ex);
        return ResponseEntity.status(500)
                .body(new ValidationErrorResponse(500, errors));
    }
}

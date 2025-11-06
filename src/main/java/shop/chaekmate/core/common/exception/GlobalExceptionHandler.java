package shop.chaekmate.core.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.chaekmate.core.common.dto.ErrorResponse;


import io.jsonwebtoken.JwtException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ErrorResponse> handleCoreException(CoreException e) {
        log.warn("[CoreException Failed] {}", e.getMessage());
        BaseErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException e) {
        log.warn("[JwtException Failed] {}", e.getMessage());
        BaseErrorCode errorCode = CommonErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("[Validation Failed] {}", e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldErrors().getFirst();
        String message = fieldError.getDefaultMessage();

        BaseErrorCode errorCode = CommonErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("[Unexpected Exception]", e);
        BaseErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode));
    }
}

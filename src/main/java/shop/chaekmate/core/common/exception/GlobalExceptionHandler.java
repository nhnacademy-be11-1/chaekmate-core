package shop.chaekmate.core.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import shop.chaekmate.common.log.logging.Log;
import shop.chaekmate.core.common.dto.ErrorResponse;


import io.jsonwebtoken.JwtException;
import shop.chaekmate.core.member.exception.*;

import java.util.Collection;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ErrorResponse> handleCoreException(CoreException e) {
        log.warn("[CoreException Failed] {}", e.getMessage());
        BaseErrorCode errorCode = e.getErrorCode();
        Log.Error(e, e.getErrorCode().getStatus().value(), "{}", e.getErrorCode().getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException e) {
        log.warn("[JwtException Failed] {}", e.getMessage());
        BaseErrorCode errorCode = CommonErrorCode.UNAUTHORIZED;
        Log.Error(e, errorCode.getStatus().value(), "{}", errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("[Validation Failed] {}", e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldErrors().getFirst();
        String message = fieldError.getDefaultMessage();
        Log.Error(e, 400, "{}", message);

        BaseErrorCode errorCode = CommonErrorCode.BAD_REQUEST;
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        log.error("[Unexpected Exception]", e);
        BaseErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        Log.Error(e, errorCode.getStatus().value(), "{}", errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode));
    }

    @ExceptionHandler(DuplicatedLoginIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedLoginIdException(DuplicatedLoginIdException e) {
        log.warn("[DuplicatedLoginIdException] {}", e.getMessage());
        MemberErrorCode errorCode = MemberErrorCode.DUPLICATED_LOGIN_ID;
        Log.Error(e, errorCode.getStatus().value(), "{}", errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode, e.getMessage()));
    }

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicatedEmailException(DuplicatedEmailException e) {
        log.warn("[DuplicatedEmailException] {}", e.getMessage());
        MemberErrorCode errorCode = MemberErrorCode.DUPLICATED_EMAIL;
        Log.Error(e, errorCode.getStatus().value(), "{}", errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(ErrorResponse.from(errorCode, e.getMessage()));
    }

    @ExceptionHandler(AddressLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleAddressLimitExceededException(AddressLimitExceededException e) {
        log.warn("[AddressLimitExceededException] {}", e.getMessage());
        Log.Error(e, e.getErrorCode().getStatus().value(), "{}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(ErrorResponse.from(e.getErrorCode()));
    }

    @ExceptionHandler(GradeConfigurationException.class)
    public ResponseEntity<ErrorResponse> handleGradeConfigurationException(GradeConfigurationException e) {
        log.error("[GradeConfigurationException] {}", e.getMessage());
        Log.Error(e, e.getErrorCode().getStatus().value(), "{}", e.getMessage());

        ErrorResponse response = ErrorResponse.from(
                e.getErrorCode(),
                "현재 시스템 설정 문제로 인해 회원가입이 불가능합니다. 잠시 후 다시 시도해주세요."
        );
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(response);
    }
}

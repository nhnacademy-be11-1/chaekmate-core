package shop.chaekmate.core.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements BaseErrorCode{

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "valid-400", "요청 값이 유효하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-401", "인증 정보가 유효하지 않습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH-401", "토큰이 유효하지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "SERVER-405", "허용되지 않은 요청 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER-500", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    CommonErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

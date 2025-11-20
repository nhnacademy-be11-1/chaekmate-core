package shop.chaekmate.core.member.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum MemberErrorCode implements BaseErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_404", "회원이 존재하지 않습니다."),
    DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "MEMBER_409_LOGIN_ID", "이미 존재하는 로그인 ID입니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "MEMBER_409_EMAIL", "이미 등록된 이메일입니다."),
    INVALID_MEMBER_REQUEST(HttpStatus.BAD_REQUEST, "MEMBER_400", "잘못된 회원 요청입니다."),
    ADDRESS_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "ADDRESS_400", "최대 10개의 주소만 등록할 수 있습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "ADDRESS_404", "주소가 존재하지 않습니다."),
    MEMBER_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_HISTORY_404", "회원 등급 기록이 존재하지 않습니다."),
    GRADE_NOT_FOUND(HttpStatus.NOT_FOUND, "GRADE_404", "등급이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    MemberErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

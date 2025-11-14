package shop.chaekmate.core.point.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;


@Getter
    public enum PointErrorCode implements BaseErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Point-policy-404", "해당하는 회원을 찾을 수 없습니다"),
    INVALID_POLICY(HttpStatus.BAD_REQUEST, "Point_Policy-400", "유효하지 않은 포인트 정책입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Point_Policy-404","활성화된 포인트 정책이 존재하지 않습니다."),
    DUPLICATED_POLICY(HttpStatus.CONFLICT, "Point_Policy-409", "이미 동일한 포인트 정책이 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    PointErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}

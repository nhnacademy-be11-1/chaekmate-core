package shop.chaekmate.core.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Schema(description = "에러 응답 공통 예시")
public record ErrorResponse(

        @Schema(description = "에러 발생 시각", example = "2025-10-29T13:00:00")
        LocalDateTime currentTime,

        @Schema(description = "HTTP 상태 코드", example = "400")
        int status,

        @Schema(description = "에러 코드", example = "valid-400")
        String code,

        @Schema(description = "에러 메시지", example = "요청 값이 유효하지 않습니다.")
        String message

) {
    public static ErrorResponse from(BaseErrorCode errorCode) {
        return new ErrorResponse(LocalDateTime.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
    public static ErrorResponse from(BaseErrorCode errorCode,String message) {
        return new ErrorResponse(LocalDateTime.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                message
        );
    }
}

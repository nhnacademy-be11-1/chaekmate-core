package shop.chaekmate.core.payment.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.core.common.dto.ErrorResponse;
import shop.chaekmate.core.payment.dto.PaymentHistoryDto;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;

import java.time.LocalDate;

@Tag(name = "결제 내역 API", description = "관리자 전체 결제 내역 조회 API")
public interface PaymentHistoryAdminControllerDocs {
    @Operation(
            summary = "결제 내역 조회 (관리자용)",
            description = """
                    관리자용 결제 내역 조회 API입니다.

                    - 날짜를 지정하지 않으면 전체 기간을 조회합니다.
                    - 1.결제 수단(PaymentMethodType)에 따라 필터링할 수 있습니다.
                    - 2.날짜(start, end)는 yyyy-MM-dd 형식으로 전달합니다.

                    - 기본값 (최근 결제부터)
                    - page: 0 (첫 페이지)
                    - size: 20 (한 페이지당 20건)
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "결제 내역 조회 성공",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentHistoryDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 (예: 날짜 형식 오류 등)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    ResponseEntity<Page<PaymentHistoryDto>> getFilteredHistories(
            @Parameter(description = "결제 수단 (예: TOSS, POINT, PAYCO 등)", example = "TOSS")
            @RequestParam(required = false) PaymentMethodType paymentType,

            @Parameter(description = "조회 시작일 (yyyy-MM-dd 형식)", example = "2025-11-01")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,

            @Parameter(description = "조회 종료일 (yyyy-MM-dd 형식)", example = "2025-11-10")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,

            @ParameterObject
            @Parameter(description = "페이징 및 정렬 정보 (page=0, size=20, sort=occurredAt,desc 기본 적용)")
            Pageable pageable
    );

}

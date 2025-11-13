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
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;

@Tag(name = "결제 내역 API", description = "관리자 전체 결제 내역 조회 API")
public interface PaymentHistoryAdminControllerDocs {
    @Operation(
            summary = "결제 내역 조회 (관리자용)",
            description = """
                    관리자용 결제 내역 조회 API입니다.
                    
                    - 결제 수단: TOSS, POINT
                    - 결제 상태: APPROVED(승인), CANCELED(취소), PARTIAL_CANCELED(부분취소), ABORTED(실패)
                    - 조회 기간: yyyy-MM-dd 형식 (둘 다 없으면 전체 기간)
                    
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
                            description = "잘못된 요청",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    ResponseEntity<Page<PaymentHistoryDto>> getFilteredHistories(
            @Parameter(description = "결제 수단 (예: TOSS, POINT, PAYCO 등)", example = "TOSS")
            @RequestParam(required = false) PaymentMethodType paymentType,

            @Parameter(description = "결제 상태 (예: APPROVED, CANCELED, PARTIAL_CANCELED, ABORTED)", example = "CANCELED")
            @RequestParam(required = false) PaymentStatusType paymentStatus,

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

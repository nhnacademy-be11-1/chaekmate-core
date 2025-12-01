package shop.chaekmate.core.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import shop.chaekmate.core.order.dto.request.CanceledBooksRequest;

public record ReturnBooksResponse(
        String orderNumber,
        long refundedAmount,              // 환불 총액 (상품금액합계 - 회수비)
        int refundedPoint,                // 환불된 포인트
        long returnFee,                   // 부과된 회수비 (0일 수도 있음)
        LocalDateTime refundedAt,
        List<CanceledBooksRequest> refundBooks
) {}

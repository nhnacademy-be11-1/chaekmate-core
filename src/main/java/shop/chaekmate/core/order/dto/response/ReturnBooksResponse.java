package shop.chaekmate.core.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import shop.chaekmate.core.order.dto.request.CanceledBooksRequest;

public record ReturnBooksResponse(
        String orderNumber,
        long returnedCash,                 //
        int returnedPoint,                // 환불된 포인트
        long returnFee,                   // 부과된 회수비 (0일 수도 있음)
        LocalDateTime returnedAt,
        List<CanceledBooksRequest> returnBooks
) {}

package shop.chaekmate.core.order.dto.request;

import java.util.List;
import shop.chaekmate.core.payment.entity.type.RefundReasonType;

public record ReturnBooksRequest(
        String orderNumber,                      // 주문번호
        RefundReasonType refundReason,      // 환불 사유
        List<CanceledBooksRequest> refundBooks  // 환불 대상 orderedBook 리스트
) { }

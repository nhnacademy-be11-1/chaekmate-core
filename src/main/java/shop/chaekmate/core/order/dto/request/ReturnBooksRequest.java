package shop.chaekmate.core.order.dto.request;

import java.util.List;
import shop.chaekmate.core.payment.entity.type.ReturnReasonType;

public record ReturnBooksRequest(
        String orderNumber,                      // 주문번호
        ReturnReasonType returnReason,      // 환불 사유
        List<CanceledBooksRequest> returnBooks  // 환불 대상 orderedBook 리스트
) { }

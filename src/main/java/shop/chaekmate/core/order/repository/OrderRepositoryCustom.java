package shop.chaekmate.core.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.chaekmate.core.order.dto.response.MemberPureAmountDto;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.type.OrderStatusType;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepositoryCustom {
    Page<Order> searchNonMemberOrder(String orderNumber, String ordererName, String ordererPhone, Pageable pageable);
    Page<Order> findMemberOrders(Long memberId, Pageable pageable);
    Page<Order> findAllOrders(OrderStatusType orderStatus, OrderedBookStatusType unitStatus, Pageable pageable);

    /**
     * 특정 기간 동안의 회원별 순수 주문금액 조회
     * 순수금액 = 상품 실구매액 - 포장비 - 배송비
     * (CANCELED, RETURNED 상태 제외)
     *
     * @param startDate 시작일시
     * @return 회원별 순수금액 목록
     */
    List<MemberPureAmountDto> calculateMemberPureAmounts(LocalDateTime startDate);
}


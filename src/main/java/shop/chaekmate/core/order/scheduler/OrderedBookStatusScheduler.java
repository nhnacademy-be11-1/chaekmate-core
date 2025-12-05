package shop.chaekmate.core.order.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.event.DeliveryEventPublisher;
import shop.chaekmate.core.order.event.ShippingCompletedEvent;
import shop.chaekmate.core.order.repository.OrderedBookRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderedBookStatusScheduler {

    private final OrderedBookRepository orderedBookRepository;
    private final DeliveryEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul") // 매일 오전 9시
    @Transactional
    public void autoCompleteShippingBooks() {

        List<OrderedBook> shippingBooks = orderedBookRepository.findShippingBooks();

        if (shippingBooks.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (OrderedBook ob : shippingBooks) {

            // 출고 시각
            LocalDateTime shippedAt = ob.getShippedAt();
            // 희망일
            LocalDateTime deliveryAt = ob.getOrder().getDeliveryAt().atStartOfDay();
            // 출고 + 1일
            LocalDateTime shippedPlus3 = shippedAt.plusDays(1);

            // 배송희망일 고려
            LocalDateTime autoCompleteAt = shippedPlus3.isAfter(deliveryAt) ? shippedPlus3 : deliveryAt;

            // 아직 자동완료 시간이 지나지 않았으면 패스
            if (now.isBefore(autoCompleteAt)) {
                continue;
            }

            // 개별 상품 배송완료
            ob.markDelivered();

            log.info("[자동배송완료] orderedBookId={}, shippedAt={}, deliveryAt={}, autoCompleteAt={}",
                    ob.getId(), shippedAt, deliveryAt, autoCompleteAt);

            // 대표 주문 상태 변경
            Long orderId = ob.getOrder().getId();
            boolean allDelivered = orderedBookRepository.isAllBooksDelivered(orderId);

            if (allDelivered) {
                ob.getOrder().markDelivered();
                eventPublisher.publishShippingCompleted(new ShippingCompletedEvent(ob.getOrder().getOrderNumber()));
                log.info("[대표주문 배송완료] orderId={}", orderId);
            }
        }
    }
}

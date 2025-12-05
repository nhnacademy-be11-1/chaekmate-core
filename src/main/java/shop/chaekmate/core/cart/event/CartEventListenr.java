package shop.chaekmate.core.cart.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.cart.service.CartService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartEventListenr {
    private final CartService cartService;

    /**
     * 로그인 이벤트 수신
     */
    @RabbitListener(queues = "cart.login.queue")
    @Transactional
    public void handleLoginSuccess(LoginSuccessEvent event) {
        try {
            log.info("로그인 이벤트 수신: memberId={}, guestId={}",
                    event.memberId(), event.guestId());

            // 장바구니 동기화
            this.cartService.loadCartOnLogin(event.memberId(), event.guestId());

            log.info("장바구니 동기화 완료: memberId={}", event.memberId());
        } catch (Exception e) {
            log.error("장바구니 동기화 실패: memberId={}, error={}",
                    event.memberId(), e.getMessage(), e);
            // 실패해도 예외를 던지지 않아 메시지는 ACK 처리됨
        }
    }

    /**
     * 로그아웃 이벤트 수신
     */
    @RabbitListener(queues = "cart.logout.queue")
    @Transactional
    public void handleLogout(LogoutEvent event) {
        try {
            log.info("로그아웃 이벤트 수신: memberId={}", event.memberId());

            // Redis 장바구니 정리
            this.cartService.clearCartOnLogout(event.memberId());

            log.info("Redis 장바구니 정리 완료: memberId={}", event.memberId());
        } catch (Exception e) {
            log.error("Redis 장바구니 정리 실패: memberId={}, error={}",
                    event.memberId(), e.getMessage(), e);
        }
    }
}

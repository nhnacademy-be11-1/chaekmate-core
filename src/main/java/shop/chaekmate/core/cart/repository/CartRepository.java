package shop.chaekmate.core.cart.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // 조회
    Optional<Cart> findByMemberId(Long memberId);

    // 삭제
    // CASCADE 옵션 설정에 따라 장바구니 아이템도 함께 삭제되는지 확인 필요
    int deleteByMemberId(Long memberId);
}

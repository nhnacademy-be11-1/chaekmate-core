package shop.chaekmate.core.cart.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.chaekmate.core.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 특정 회원의 Cart 조회
     *
     * @param memberId Member ID
     * @return Cart (Optional)
     */
    @Query("SELECT c FROM Cart c WHERE c.member.id = :memberId")
    Optional<Cart> findByMemberId(@Param("memberId") Long memberId);
}

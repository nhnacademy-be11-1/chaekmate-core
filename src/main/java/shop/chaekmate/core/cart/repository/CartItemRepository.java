package shop.chaekmate.core.cart.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemRepositoryCustom {
    // 조회
    boolean existsByCartIdAndBookId(Long cartId, Long bookId);

    // 정렬
    List<CartItem> findAllByCartIdOrderByCreatedAtAsc (Long cartId);
    List<CartItem> findAllByCartIdOrderByCreatedAtDesc (Long cartId);

    // 삭제
    int deleteByCartIdAndBookId(Long cartId, Long bookId);
    int deleteAllByCartId(Long cartId);
}

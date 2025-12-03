package shop.chaekmate.core.cart.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.chaekmate.core.cart.entity.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemRepositoryCustom {

    /**
     * 특정 Cart의 특정 도서 아이템 조회
     *
     * @param cartId Cart ID
     * @param bookId Book ID
     * @return 해당 아이템 (Optional)
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.book.id = :bookId")
    Optional<CartItem> findByCartIdAndBookId(@Param("cartId") Long cartId, @Param("bookId") Long bookId);

    /**
     * 특정 Cart의 특정 도서 아이템 삭제 (Soft Delete)
     *
     * @param cartId Cart ID
     * @param bookId Book ID
     */
    @Modifying
    @Query("UPDATE CartItem ci SET ci.deletedAt = CURRENT_TIMESTAMP WHERE ci.cart.id = :cartId AND ci.book.id = :bookId AND ci.deletedAt IS NULL")
    void deleteByCartIdAndBookId(@Param("cartId") Long cartId, @Param("bookId") Long bookId);

    /**
     * 특정 Cart의 여러 도서 아이템 일괄 삭제 (Soft Delete)
     *
     * @param cartId Cart ID
     * @param bookIds Book ID 리스트
     */
    @Modifying
    @Query("UPDATE CartItem ci SET ci.deletedAt = CURRENT_TIMESTAMP WHERE ci.cart.id = :cartId AND ci.book.id IN :bookIds AND ci.deletedAt IS NULL")
    void deleteByCartIdAndBookIds(@Param("cartId") Long cartId, @Param("bookIds") List<Long> bookIds);


    /**
     * 특정 Cart의 모든 장바구니 아이템 삭제 (Soft Delete)
     *
     * @param cartId Cart ID
     */
    @Modifying
    @Query("UPDATE CartItem ci SET ci.deletedAt = CURRENT_TIMESTAMP WHERE ci.cart.id = :cartId AND ci.deletedAt IS NULL")
    void deleteAllByCartId(@Param("cartId") Long cartId);
}
package shop.chaekmate.core.cart.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum CartErrorCode implements BaseErrorCode {

    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART-404", "해당 장바구니를 찾을 수 없습니다."),
    BOOK_INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "CART-ITEM-400", "해당 도서의 재고가 부족합니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CART-ITEM-404", "해당 장바구니 아이템을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    CartErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

package shop.chaekmate.core.cart.exception.cartitem;

import shop.chaekmate.core.common.exception.CoreException;

public class CartItemInvalidQuantityException extends CoreException {
  public CartItemInvalidQuantityException() {
    super(CartItemErrorCode.INVALID_QUANTITY);
  }
}

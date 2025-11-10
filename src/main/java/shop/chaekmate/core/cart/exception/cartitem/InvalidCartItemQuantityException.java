package shop.chaekmate.core.cart.exception.cartitem;

import shop.chaekmate.core.common.exception.CoreException;

public class InvalidCartItemQuantityException extends CoreException {
  public InvalidCartItemQuantityException() {
    super(CartItemErrorCode.INVALID_QUANTITY);
  }
}

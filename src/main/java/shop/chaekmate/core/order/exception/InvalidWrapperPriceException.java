package shop.chaekmate.core.order.exception;

public class InvalidWrapperPriceException extends RuntimeException {
    public InvalidWrapperPriceException() {
        super("포장지 가격 음수 불가");
    }
}

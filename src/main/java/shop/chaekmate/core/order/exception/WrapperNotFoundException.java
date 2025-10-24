package shop.chaekmate.core.order.exception;

public class WrapperNotFoundException extends RuntimeException {
    public WrapperNotFoundException(Long id) {
        super("해당 포장지를 찾을 수 없습니다. : " + id);
    }
}

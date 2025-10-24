package shop.chaekmate.core.order.exception;

public class DuplicatedWrapperNameException extends RuntimeException {
    public DuplicatedWrapperNameException(String name) {
        super("이미 존재하는 포장지입니다. : " + name);
    }
}

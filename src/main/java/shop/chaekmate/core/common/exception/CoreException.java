package shop.chaekmate.core.common.exception;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException{

    private final BaseErrorCode errorCode;

    public CoreException(BaseErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}

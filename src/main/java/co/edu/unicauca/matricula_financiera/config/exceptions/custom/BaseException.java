package co.edu.unicauca.matricula_financiera.config.exceptions.custom;

import co.edu.unicauca.matricula_financiera.config.exceptions.structure.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] args;

    protected BaseException(ErrorCode errorCode, String messageKey, Object... args) {
        super(messageKey);
        this.errorCode = errorCode;
        this.args = (args == null) ? new Object[]{} : args;
    }
}

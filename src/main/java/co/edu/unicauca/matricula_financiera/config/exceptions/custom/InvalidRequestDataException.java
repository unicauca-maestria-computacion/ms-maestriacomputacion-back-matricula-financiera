package co.edu.unicauca.matricula_financiera.config.exceptions.custom;

import co.edu.unicauca.matricula_financiera.config.exceptions.structure.ErrorCode;

public class InvalidRequestDataException extends BaseException {
    public InvalidRequestDataException(String messageKey, Object... args) {
        super(ErrorCode.INVALID_REQUEST_DATA, messageKey, args);
    }
}

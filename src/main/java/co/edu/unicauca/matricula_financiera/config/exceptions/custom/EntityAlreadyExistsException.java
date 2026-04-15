package co.edu.unicauca.matricula_financiera.config.exceptions.custom;

import co.edu.unicauca.matricula_financiera.config.exceptions.structure.ErrorCode;

public class EntityAlreadyExistsException extends BaseException {
    public EntityAlreadyExistsException(String messageKey, Object... args) {
        super(ErrorCode.ENTITY_ALREADY_EXISTS, messageKey, args);
    }
}

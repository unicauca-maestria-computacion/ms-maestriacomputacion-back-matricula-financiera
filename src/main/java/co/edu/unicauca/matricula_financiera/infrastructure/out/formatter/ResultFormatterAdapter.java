package co.edu.unicauca.matricula_financiera.infrastructure.out.formatter;

import co.edu.unicauca.matricula_financiera.config.exceptions.custom.BusinessRuleViolatedException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.EntityAlreadyExistsException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.EntityNotFoundException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.InvalidRequestDataException;
import co.edu.unicauca.matricula_financiera.domain.ports.out.ResultFormatterPort;
import org.springframework.stereotype.Component;

@Component
public class ResultFormatterAdapter implements ResultFormatterPort {

    @Override
    public void errorEntityNotFound(String messageKey, Object... args) {
        throw new EntityNotFoundException(messageKey, args);
    }

    @Override
    public void errorEntityAlreadyExists(String messageKey, Object... args) {
        throw new EntityAlreadyExistsException(messageKey, args);
    }

    @Override
    public void errorBusinessRuleViolated(String messageKey, Object... args) {
        throw new BusinessRuleViolatedException(messageKey, args);
    }

    @Override
    public void errorInvalidRequestData(String messageKey, Object... args) {
        throw new InvalidRequestDataException(messageKey, args);
    }

    @Override
    public void errorInternalFailure(String messageKey, Object... args) {
        throw new RuntimeException(messageKey);
    }
}

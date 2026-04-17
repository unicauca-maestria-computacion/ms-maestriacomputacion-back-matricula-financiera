package co.edu.unicauca.matricula_financiera.domain.ports.out;

public interface ResultFormatterPort {
    void errorEntityNotFound(String messageKey, Object... args);
    void errorEntityAlreadyExists(String messageKey, Object... args);
    void errorBusinessRuleViolated(String messageKey, Object... args);
    void errorInvalidRequestData(String messageKey, Object... args);
    void errorInternalFailure(String messageKey, Object... args);
}

package co.edu.unicauca.matricula_financiera.config.exceptions.custom;

import co.edu.unicauca.matricula_financiera.config.exceptions.structure.ErrorCode;

public class BusinessRuleViolatedException extends BaseException {
    public BusinessRuleViolatedException(String messageKey, Object... args) {
        super(ErrorCode.BUSINESS_RULE_VIOLATED, messageKey, args);
    }
}

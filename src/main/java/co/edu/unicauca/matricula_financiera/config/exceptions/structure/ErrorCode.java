package co.edu.unicauca.matricula_financiera.config.exceptions.structure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    GENERIC_ERROR          ("MF-0001", "error.generic"),
    ENTITY_ALREADY_EXISTS  ("MF-0002", "error.entity.exists"),
    ENTITY_NOT_FOUND       ("MF-0003", "error.entity.notFound"),
    DENIED_STATE           ("MF-0004", "error.state.denied"),
    BUSINESS_RULE_VIOLATED ("MF-0005", "error.businessRule.violated"),
    INVALID_REQUEST_DATA   ("MF-0006", "error.request.invalid");

    private final String code;
    private final String messageKey;
}

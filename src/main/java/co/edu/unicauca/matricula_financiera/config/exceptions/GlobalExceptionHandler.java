package co.edu.unicauca.matricula_financiera.config.exceptions;

import co.edu.unicauca.matricula_financiera.config.exceptions.custom.BusinessRuleViolatedException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.EntityAlreadyExistsException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.EntityNotFoundException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.InvalidRequestDataException;
import co.edu.unicauca.matricula_financiera.config.exceptions.structure.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception on {} {}", req.getMethod(), req.getRequestURI(), ex);
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERIC_ERROR, req,
                ErrorCode.GENERIC_ERROR.getMessageKey());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleNotFound(EntityNotFoundException ex, HttpServletRequest req) {
        return buildProblem(HttpStatus.NOT_FOUND, ex.getErrorCode(), req,
                ex.getMessage(), ex.getArgs());
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(EntityAlreadyExistsException ex, HttpServletRequest req) {
        return buildProblem(HttpStatus.CONFLICT, ex.getErrorCode(), req,
                ex.getMessage(), ex.getArgs());
    }

    @ExceptionHandler(BusinessRuleViolatedException.class)
    public ProblemDetail handleBusinessRule(BusinessRuleViolatedException ex, HttpServletRequest req) {
        return buildProblem(HttpStatus.UNPROCESSABLE_ENTITY, ex.getErrorCode(), req,
                ex.getMessage(), ex.getArgs());
    }

    @ExceptionHandler(InvalidRequestDataException.class)
    public ProblemDetail handleInvalidRequest(InvalidRequestDataException ex, HttpServletRequest req) {
        return buildProblem(HttpStatus.BAD_REQUEST, ex.getErrorCode(), req,
                ex.getMessage(), ex.getArgs());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ProblemDetail pd = buildProblem(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST_DATA, req,
                ErrorCode.INVALID_REQUEST_DATA.getMessageKey());
        pd.setProperty("validationErrors", details);
        return pd;
    }

    private ProblemDetail buildProblem(HttpStatus status, ErrorCode errorCode,
            HttpServletRequest req, String messageKey, Object... args) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, resolveMessage(messageKey, args));
        pd.setProperty("errorCode", errorCode.getCode());
        pd.setProperty("url", req.getRequestURI());
        pd.setProperty("method", req.getMethod());
        return pd;
    }

    private String resolveMessage(String key, Object... args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return key;
        }
    }
}

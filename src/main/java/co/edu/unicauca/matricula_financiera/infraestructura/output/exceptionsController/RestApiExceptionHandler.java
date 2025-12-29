package co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController;


import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.exceptionsStructure.Error;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.exceptionsStructure.ErrorUtils;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions.EntidadNoExisteException;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions.EntidadYaExisteException;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions.ReglaNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class RestApiExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGenericException(final HttpServletRequest req,
                                                        final Exception ex,
                                                        final Locale locale) {
        Error error = ErrorUtils.crearError(
            "ERROR_GENERICO",
            ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            req.getRequestURL().toString(),
            req.getMethod()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(EntidadYaExisteException.class)
    public ResponseEntity<Error> handleEntidadYaExisteException(final HttpServletRequest req,
                                                                final EntidadYaExisteException ex) {
        Error error = ErrorUtils.crearError(
            "ENTIDAD_YA_EXISTE",
            ex.getLlaveMensaje(),
            HttpStatus.CONFLICT.value(),
            req.getRequestURL().toString(),
            req.getMethod()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Error> handleReglaNegocioExcepcion(final HttpServletRequest req, 
                                                             final ReglaNegocioException ex, 
                                                             final Locale locale) {
        Error error = ErrorUtils.crearError(
            "VIOLACION_REGLA_DE_NEGOCIO",
            ex.getReglaNegocio(),
            HttpStatus.BAD_REQUEST.value(),
            req.getRequestURL().toString(),
            req.getMethod()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(EntidadNoExisteException.class)
    public ResponseEntity<Error> handleEntidadNoExisteException(final HttpServletRequest req, 
                                                               final EntidadNoExisteException ex, 
                                                               final Locale locale) {
        Error error = ErrorUtils.crearError(
            "ENTIDAD_NO_ENCONTRADA",
            ex.getLlaveMensaje(),
            HttpStatus.NOT_FOUND.value(),
            req.getRequestURL().toString(),
            req.getMethod()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> 
            errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}


package co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController;

import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.exceptionsStructure.CodigoError;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions.EntidadNoExisteException;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions.EntidadYaExisteException;
import co.edu.unicauca.matricula_financiera.infraestructura.output.exceptionsController.ownExceptions.ReglaNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestApiExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(final HttpServletRequest req,
                                                                final Exception ex,
                                                                final Locale locale) {
        ex.printStackTrace();
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        pd.setProperty("errorCode", CodigoError.ERROR_GENERICO.name());
        pd.setProperty("url", req.getRequestURI());
        pd.setProperty("method", req.getMethod());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(pd);
    }

    @ExceptionHandler(EntidadYaExisteException.class)
    public ResponseEntity<ProblemDetail> handleEntidadYaExisteException(final HttpServletRequest req,
                                                                        final EntidadYaExisteException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMensaje());
        pd.setProperty("errorCode", CodigoError.ENTIDAD_YA_EXISTE.name());
        pd.setProperty("url", req.getRequestURI());
        pd.setProperty("method", req.getMethod());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ProblemDetail> handleReglaNegocioExcepcion(final HttpServletRequest req,
                                                                     final ReglaNegocioException ex,
                                                                     final Locale locale) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getReglaNegocio());
        pd.setProperty("errorCode", CodigoError.VIOLACION_REGLA_DE_NEGOCIO.name());
        pd.setProperty("url", req.getRequestURI());
        pd.setProperty("method", req.getMethod());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(EntidadNoExisteException.class)
    public ResponseEntity<ProblemDetail> handleEntidadNoExisteException(final HttpServletRequest req,
                                                                        final EntidadNoExisteException ex,
                                                                        final Locale locale) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMensaje());
        pd.setProperty("errorCode", CodigoError.ENTIDAD_NO_ENCONTRADA.name());
        pd.setProperty("url", req.getRequestURI());
        pd.setProperty("method", req.getMethod());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(final HttpServletRequest req,
                                                                    final MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setProperty("errorCode", CodigoError.ERROR_VALIDACION.name());
        pd.setProperty("url", req.getRequestURI());
        pd.setProperty("method", req.getMethod());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(final HttpServletRequest req,
                                                                            final ConstraintViolationException ex) {
        String detail = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setProperty("errorCode", CodigoError.ERROR_VALIDACION.name());
        pd.setProperty("url", req.getRequestURI());
        pd.setProperty("method", req.getMethod());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }
}

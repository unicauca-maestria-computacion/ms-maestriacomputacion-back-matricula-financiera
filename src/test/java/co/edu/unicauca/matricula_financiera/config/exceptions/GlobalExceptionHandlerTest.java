package co.edu.unicauca.matricula_financiera.config.exceptions;

import co.edu.unicauca.matricula_financiera.config.exceptions.custom.BusinessRuleViolatedException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.EntityAlreadyExistsException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.EntityNotFoundException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.InvalidRequestDataException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias del manejador global de excepciones.
 *
 * Se ejercita la clase directamente, sin levantar el contexto de Spring: la
 * unica dependencia externa (el MessageSource) se sustituye por una
 * implementacion en memoria. Se verifica el mapeo excepcion -> codigo HTTP,
 * la presencia del codigo de error de negocio en el cuerpo y la resolucion
 * de los mensajes con interpolacion de argumentos.
 */
@DisplayName("GlobalExceptionHandler - mapeo de excepciones a respuestas HTTP")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        StaticMessageSource messages = new StaticMessageSource();
        // El patron {1,number,#} reproduce el de ValidationMessages.properties.
        // Sin el, MessageFormat aplica el separador de miles de la configuracion
        // regional y el anio 2024 se presenta al usuario como "2.024".
        messages.addMessage("validation.period.notFound", Locale.getDefault(),
                "El periodo academico {0}-{1,number,#} no existe.");
        messages.addMessage("validation.period.notNull", Locale.getDefault(),
                "El periodo academico no puede ser nulo.");
        messages.addMessage("error.generic", Locale.getDefault(),
                "Ocurrio un error inesperado.");

        handler = new GlobalExceptionHandler(messages);

        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/v1/gestion-matricula-financiera/estudiantes");
    }

    @Test
    @DisplayName("EntityNotFoundException produce 404 con codigo MF-0003")
    void entityNotFound_producesNotFound() {
        ProblemDetail pd = handler.handleNotFound(
                new EntityNotFoundException("validation.period.notFound", 1, 2024), request);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(pd.getProperties()).containsEntry("errorCode", "MF-0003");
        assertThat(pd.getDetail()).isEqualTo("El periodo academico 1-2024 no existe.");
    }

    @Test
    @DisplayName("BusinessRuleViolatedException produce 422 con codigo MF-0005")
    void businessRule_producesUnprocessableEntity() {
        ProblemDetail pd = handler.handleBusinessRule(
                new BusinessRuleViolatedException("validation.period.notNull"), request);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(pd.getProperties()).containsEntry("errorCode", "MF-0005");
    }

    @Test
    @DisplayName("EntityAlreadyExistsException produce 409 con codigo MF-0002")
    void alreadyExists_producesConflict() {
        ProblemDetail pd = handler.handleAlreadyExists(
                new EntityAlreadyExistsException("error.entity.exists"), request);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(pd.getProperties()).containsEntry("errorCode", "MF-0002");
    }

    @Test
    @DisplayName("InvalidRequestDataException produce 400 con codigo MF-0006")
    void invalidRequest_producesBadRequest() {
        ProblemDetail pd = handler.handleInvalidRequest(
                new InvalidRequestDataException("error.request.invalid"), request);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(pd.getProperties()).containsEntry("errorCode", "MF-0006");
    }

    @Test
    @DisplayName("Cualquier otra excepcion produce 500 con codigo MF-0001")
    void unexpectedException_producesInternalServerError() {
        ProblemDetail pd = handler.handleGeneric(new IllegalStateException("fallo inesperado"), request);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(pd.getProperties()).containsEntry("errorCode", "MF-0001");
        assertThat(pd.getDetail()).isEqualTo("Ocurrio un error inesperado.");
    }

    @Test
    @DisplayName("La respuesta conserva la ruta y el metodo de la peticion")
    void response_keepsRequestContext() {
        ProblemDetail pd = handler.handleNotFound(
                new EntityNotFoundException("validation.period.notFound", 1, 2024), request);

        assertThat(pd.getProperties())
                .containsEntry("url", "/api/v1/gestion-matricula-financiera/estudiantes")
                .containsEntry("method", "POST");
    }

    @Test
    @DisplayName("Una clave de mensaje no registrada se devuelve tal cual, sin romper la respuesta")
    void unknownMessageKey_isReturnedVerbatim() {
        ProblemDetail pd = handler.handleBusinessRule(
                new BusinessRuleViolatedException("clave.inexistente"), request);

        assertThat(pd.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(pd.getDetail()).isEqualTo("clave.inexistente");
    }
}

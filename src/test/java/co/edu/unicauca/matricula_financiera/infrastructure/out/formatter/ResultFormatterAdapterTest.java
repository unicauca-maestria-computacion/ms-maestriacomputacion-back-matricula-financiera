package co.edu.unicauca.matricula_financiera.infrastructure.out.formatter;

import co.edu.unicauca.matricula_financiera.config.exceptions.custom.BusinessRuleViolatedException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.EntityAlreadyExistsException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.EntityNotFoundException;
import co.edu.unicauca.matricula_financiera.config.exceptions.custom.InvalidRequestDataException;
import co.edu.unicauca.matricula_financiera.config.exceptions.structure.ErrorCode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

/**
 * Pruebas unitarias del formateador de resultados.
 *
 * Esta clase es la frontera que permite que la capa de aplicacion senale
 * condiciones de error sin conocer las clases de excepcion definidas en el
 * paquete de configuracion (seccion 3.8.2 del documento). La prueba verifica
 * que cada metodo del puerto lance la excepcion correcta, conserve la clave
 * de mensaje y propague los argumentos de interpolacion.
 */
@DisplayName("ResultFormatterAdapter - traduccion de errores de dominio")
class ResultFormatterAdapterTest {

    private ResultFormatterAdapter formatter;

    @BeforeEach
    void setUp() {
        formatter = new ResultFormatterAdapter();
    }

    @Test
    @DisplayName("errorEntityNotFound lanza EntityNotFoundException con codigo MF-0003")
    void errorEntityNotFound_throwsWithCorrectCode() {
        EntityNotFoundException ex = catchThrowableOfType(
                () -> formatter.errorEntityNotFound("validation.period.notFound", 1, 2024),
                EntityNotFoundException.class);

        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo("validation.period.notFound");
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND);
        assertThat(ex.getErrorCode().getCode()).isEqualTo("MF-0003");
        assertThat(ex.getArgs()).containsExactly(1, 2024);
    }

    @Test
    @DisplayName("errorBusinessRuleViolated lanza BusinessRuleViolatedException con codigo MF-0005")
    void errorBusinessRuleViolated_throwsWithCorrectCode() {
        BusinessRuleViolatedException ex = catchThrowableOfType(
                () -> formatter.errorBusinessRuleViolated("validation.period.notNull"),
                BusinessRuleViolatedException.class);

        assertThat(ex).isNotNull();
        assertThat(ex.getMessage()).isEqualTo("validation.period.notNull");
        assertThat(ex.getErrorCode().getCode()).isEqualTo("MF-0005");
    }

    @Test
    @DisplayName("errorEntityAlreadyExists lanza EntityAlreadyExistsException con codigo MF-0002")
    void errorEntityAlreadyExists_throwsWithCorrectCode() {
        EntityAlreadyExistsException ex = catchThrowableOfType(
                () -> formatter.errorEntityAlreadyExists("error.entity.exists", "EST001"),
                EntityAlreadyExistsException.class);

        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode().getCode()).isEqualTo("MF-0002");
        assertThat(ex.getArgs()).containsExactly("EST001");
    }

    @Test
    @DisplayName("errorInvalidRequestData lanza InvalidRequestDataException con codigo MF-0006")
    void errorInvalidRequestData_throwsWithCorrectCode() {
        InvalidRequestDataException ex = catchThrowableOfType(
                () -> formatter.errorInvalidRequestData("error.request.invalid"),
                InvalidRequestDataException.class);

        assertThat(ex).isNotNull();
        assertThat(ex.getErrorCode().getCode()).isEqualTo("MF-0006");
    }

    @Test
    @DisplayName("errorInternalFailure lanza una excepcion no controlada")
    void errorInternalFailure_throwsRuntimeException() {
        assertThatThrownBy(() -> formatter.errorInternalFailure("error.generic"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("error.generic");
    }

    @Test
    @DisplayName("La ausencia de argumentos produce un arreglo vacio, nunca null")
    void whenNoArgs_argsArrayIsEmptyNotNull() {
        EntityNotFoundException ex = catchThrowableOfType(
                () -> formatter.errorEntityNotFound("validation.student.code.notFound"),
                EntityNotFoundException.class);

        assertThat(ex).isNotNull();
        assertThat(ex.getArgs()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Ningun metodo del formateador retorna con normalidad")
    void noMethodReturnsNormally() {
        assertThatThrownBy(() -> formatter.errorEntityNotFound("k")).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> formatter.errorEntityAlreadyExists("k")).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> formatter.errorBusinessRuleViolated("k")).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> formatter.errorInvalidRequestData("k")).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> formatter.errorInternalFailure("k")).isInstanceOf(RuntimeException.class);
    }
}

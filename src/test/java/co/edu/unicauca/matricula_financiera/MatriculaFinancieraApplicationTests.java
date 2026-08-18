package co.edu.unicauca.matricula_financiera;

import co.edu.unicauca.matricula_financiera.application.usecases.ManageEnrolledStudentsUseCaseImpl;
import co.edu.unicauca.matricula_financiera.domain.ports.in.ManageEnrolledStudentsUseCase;
import co.edu.unicauca.matricula_financiera.domain.ports.out.ResultFormatterPort;
import co.edu.unicauca.matricula_financiera.domain.ports.out.StudentGatewayPort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verificacion de la independencia de la capa de aplicacion respecto del
 * contenedor de Spring.
 *
 * Nota: la version anterior de esta clase declaraba un metodo contextLoads()
 * sin la anotacion @SpringBootTest, por lo que no cargaba el contexto ni
 * verificaba nada. La carga completa del contexto se comprueba ahora en las
 * pruebas de integracion (EstudiantesApiIT).
 */
@DisplayName("Capa de aplicacion - independencia del contenedor")
class MatriculaFinancieraApplicationTests {

    @Test
    @DisplayName("El caso de uso se instancia como objeto Java simple a partir de sus puertos")
    void useCaseIsAPlainJavaObject() {
        StudentGatewayPort gateway = Mockito.mock(StudentGatewayPort.class);
        ResultFormatterPort formatter = Mockito.mock(ResultFormatterPort.class);

        ManageEnrolledStudentsUseCase useCase =
                new ManageEnrolledStudentsUseCaseImpl(gateway, formatter);

        assertThat(useCase).isNotNull();
        assertThat(useCase.getAcademicPeriods()).isEmpty();
    }

    @Test
    @DisplayName("La implementacion del caso de uso no declara anotaciones de Spring")
    void useCaseImplHasNoSpringStereotypes() {
        assertThat(ManageEnrolledStudentsUseCaseImpl.class.getAnnotations())
                .noneMatch(a -> a.annotationType().getPackageName().startsWith("org.springframework"));
    }
}

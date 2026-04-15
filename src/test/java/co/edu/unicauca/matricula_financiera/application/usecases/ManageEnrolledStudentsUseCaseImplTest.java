package co.edu.unicauca.matricula_financiera.application.usecases;

import co.edu.unicauca.matricula_financiera.domain.ports.out.ResultFormatterPort;
import co.edu.unicauca.matricula_financiera.domain.ports.out.StudentGatewayPort;
import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.Materia;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageEnrolledStudentsUseCaseImplTest {

    @Mock
    private StudentGatewayPort gateway;

    @Mock
    private ResultFormatterPort formatter;

    @InjectMocks
    private ManageEnrolledStudentsUseCaseImpl useCase;

    private PeriodoAcademico validPeriod;
    private Estudiante studentSemester3;
    private Estudiante studentSemester5WithTg2;
    private Estudiante studentSemester5NoSubjects;
    private Estudiante studentSemester5WithOtherSubjects;

    @BeforeEach
    void setUp() {
        validPeriod = new PeriodoAcademico();
        validPeriod.setId(1L);
        validPeriod.setTagPeriodo(1);
        validPeriod.setAño(2024);

        studentSemester3 = new Estudiante();
        studentSemester3.setId(1L);
        studentSemester3.setCodigo("EST001");
        studentSemester3.setSemestreFinanciero(3);

        studentSemester5WithTg2 = new Estudiante();
        studentSemester5WithTg2.setId(2L);
        studentSemester5WithTg2.setCodigo("EST002");
        studentSemester5WithTg2.setSemestreFinanciero(5);

        studentSemester5NoSubjects = new Estudiante();
        studentSemester5NoSubjects.setId(3L);
        studentSemester5NoSubjects.setCodigo("EST003");
        studentSemester5NoSubjects.setSemestreFinanciero(5);

        studentSemester5WithOtherSubjects = new Estudiante();
        studentSemester5WithOtherSubjects.setId(4L);
        studentSemester5WithOtherSubjects.setCodigo("EST004");
        studentSemester5WithOtherSubjects.setSemestreFinanciero(5);
    }

    @Test
    void getStudentsByPeriod_whenPeriodIsNull_shouldCallFormatterError() {
        useCase.getStudentsByPeriod(null);

        verify(formatter).errorBusinessRuleViolated(anyString());
    }

    @Test
    void getStudentsByPeriod_whenPeriodNotFound_shouldCallFormatterNotFound() {
        when(gateway.existsAcademicPeriod(any())).thenReturn(false);

        useCase.getStudentsByPeriod(validPeriod);

        verify(formatter).errorEntityNotFound(anyString(), anyInt(), anyInt());
    }

    @Test
    void getStudentsByPeriod_whenStudentHasSemester3_shouldReturnSmlv6() {
        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(validPeriod);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(studentSemester3));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        List<Estudiante> result = useCase.getStudentsByPeriod(validPeriod);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValorEnSMLV()).isEqualTo(6);
    }

    @Test
    void getStudentsByPeriod_whenStudentHasSemester5WithOnlyTg2_shouldReturnSmlv1() {
        Materia tg2 = new Materia();
        tg2.setMateria("Trabajo de Grado 2");
        MatriculaAcademica enrollment = new MatriculaAcademica();
        enrollment.setMaterias(List.of(tg2));

        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(validPeriod);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(studentSemester5WithTg2));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of(enrollment));

        List<Estudiante> result = useCase.getStudentsByPeriod(validPeriod);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValorEnSMLV()).isEqualTo(1);
    }

    @Test
    void getStudentsByPeriod_whenStudentHasSemester5WithNoSubjects_shouldReturnSmlvNull() {
        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(validPeriod);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(studentSemester5NoSubjects));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        List<Estudiante> result = useCase.getStudentsByPeriod(validPeriod);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValorEnSMLV()).isNull();
    }

    @Test
    void getStudentsByPeriod_whenStudentHasSemester5WithOtherSubjects_shouldReturnSmlv6() {
        Materia other = new Materia();
        other.setMateria("Algoritmos Avanzados");
        MatriculaAcademica enrollment = new MatriculaAcademica();
        enrollment.setMaterias(List.of(other));

        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(validPeriod);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(studentSemester5WithOtherSubjects));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of(enrollment));

        List<Estudiante> result = useCase.getStudentsByPeriod(validPeriod);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValorEnSMLV()).isEqualTo(6);
    }

    @Test
    void getStudentByCode_whenCodeIsNull_shouldCallFormatterError() {
        useCase.getStudentByCode(null, null, null);

        verify(formatter).errorBusinessRuleViolated(anyString());
    }

    @Test
    void getStudentByCode_whenStudentNotFound_shouldCallFormatterNotFound() {
        when(gateway.existsStudentByCode(anyString())).thenReturn(false);

        useCase.getStudentByCode("NOEXISTE", null, null);

        verify(formatter).errorEntityNotFound(anyString(), any());
    }

    @Test
    void getAcademicPeriods_shouldDelegateToGateway() {
        when(gateway.findAllPeriods()).thenReturn(List.of(validPeriod));

        List<PeriodoAcademico> result = useCase.getAcademicPeriods();

        assertThat(result).hasSize(1);
        verify(gateway).findAllPeriods();
    }
}

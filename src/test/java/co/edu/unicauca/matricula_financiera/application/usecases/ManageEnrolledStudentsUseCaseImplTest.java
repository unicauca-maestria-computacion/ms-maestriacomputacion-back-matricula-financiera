package co.edu.unicauca.matricula_financiera.application.usecases;

import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.Materia;
import co.edu.unicauca.matricula_financiera.domain.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.domain.ports.out.ResultFormatterPort;
import co.edu.unicauca.matricula_financiera.domain.ports.out.StudentGatewayPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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

    /**
     * Un estudiante entre el quinto y el octavo semestre sin asignaturas
     * registradas paga el valor ordinario de 6 SMLV, conforme a la Tabla 3.7
     * del documento. La ausencia de asignaturas no exime del pago: solo la
     * matrícula exclusiva de Trabajo de Grado 2 da lugar al valor reducido.
     *
     * Nota: esta prueba esperaba anteriormente un valor nulo, criterio que
     * dejó de corresponder con la implementación y que la mantenía en estado
     * fallido.
     */
    @Test
    void getStudentsByPeriod_whenStudentHasSemester5WithNoSubjects_shouldReturnSmlv6() {
        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(validPeriod);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(studentSemester5NoSubjects));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        List<Estudiante> result = useCase.getStudentsByPeriod(validPeriod);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getValorEnSMLV()).isEqualTo(6);
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

    @Test
    void enrich_shouldCallGatewayWithCorrectParamsAndAssignBecasDescuentos() {
        // Arrange
        PeriodoAcademico periodoConFechas = new PeriodoAcademico();
        periodoConFechas.setId(1L);
        periodoConFechas.setTagPeriodo(1);
        periodoConFechas.setAño(2024);
        periodoConFechas.setFechaInicio(LocalDate.of(2024, 1, 15));
        periodoConFechas.setFechaFin(LocalDate.of(2024, 6, 30));

        co.edu.unicauca.matricula_financiera.domain.models.BecaDescuentoInfo beca =
                new co.edu.unicauca.matricula_financiera.domain.models.BecaDescuentoInfo(
                        "BECA", 50.0f, "RES-001", "avalada", "SI");

        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(periodoConFechas);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(studentSemester3));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of());
        when(gateway.findBecasDescuentosByEstudianteAndPeriodo(
                anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(beca));

        // Act
        List<Estudiante> result = useCase.getStudentsByPeriod(validPeriod);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBecasDescuentos()).hasSize(1);
        assertThat(result.get(0).getBecasDescuentos().get(0).getTipo()).isEqualTo("BECA");
        verify(gateway).findBecasDescuentosByEstudianteAndPeriodo(
                anyLong(),
                org.mockito.ArgumentMatchers.eq(LocalDate.of(2024, 1, 15)),
                org.mockito.ArgumentMatchers.eq(LocalDate.of(2024, 6, 30)));
    }

    @Test
    void enrich_whenPeriodHasNoFechas_shouldAssignEmptyBecasDescuentos() {
        // Arrange - período sin fechas
        PeriodoAcademico periodoSinFechas = new PeriodoAcademico();
        periodoSinFechas.setId(1L);
        periodoSinFechas.setTagPeriodo(1);
        periodoSinFechas.setAño(2024);
        // sin setFechaInicio ni setFechaFin

        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(periodoSinFechas);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(studentSemester3));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        // Act
        List<Estudiante> result = useCase.getStudentsByPeriod(validPeriod);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBecasDescuentos()).isEmpty();
    }

    // =================================================================
    // Reglas de negocio adicionales verificadas en el Capítulo 4
    // =================================================================

    /**
     * Acuerdo 044 de 2012: los estudiantes que superan la duración prevista del
     * programa pagan un salario mínimo, con independencia de las asignaturas
     * que tengan registradas.
     */
    @Test
    void getStudentsByPeriod_whenFinancialSemesterIsNineOrMore_shouldReturnSmlv1() {
        PeriodoAcademico periodoConsulta = new PeriodoAcademico();
        periodoConsulta.setId(9L);
        periodoConsulta.setTagPeriodo(2);
        periodoConsulta.setAño(2024);

        Estudiante estudianteAntiguo = new Estudiante();
        estudianteAntiguo.setId(9L);
        estudianteAntiguo.setCodigo("EST009");
        // Ingreso 2020-1, consulta 2024-2 => (2024-2020)*2 + (2-1) + 1 = 10 semestres
        estudianteAntiguo.setPeriodoIngreso("2020-1");

        Materia otra = new Materia();
        otra.setMateria("Algoritmos Avanzados");
        MatriculaAcademica matricula = new MatriculaAcademica();
        matricula.setMaterias(List.of(otra));

        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(periodoConsulta);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(estudianteAntiguo));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of(matricula));

        List<Estudiante> result = useCase.getStudentsByPeriod(periodoConsulta);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSemestreFinanciero()).isEqualTo(10);
        assertThat(result.get(0).getValorEnSMLV()).isEqualTo(1);
    }

    /**
     * El semestre académico se acota a cuatro porque el plan de estudios de la
     * maestría contempla cuatro semestres, mientras que el semestre financiero
     * continúa creciendo. Esta distinción es la que recoge el Capítulo 2.
     */
    @Test
    void getStudentsByPeriod_academicSemesterIsCappedAtFour_financialSemesterIsNot() {
        PeriodoAcademico periodoConsulta = new PeriodoAcademico();
        periodoConsulta.setId(9L);
        periodoConsulta.setTagPeriodo(1);
        periodoConsulta.setAño(2024);

        Estudiante estudiante = new Estudiante();
        estudiante.setId(9L);
        estudiante.setCodigo("EST009");
        // Ingreso 2021-1, consulta 2024-1 => (2024-2021)*2 + 0 + 1 = 7 semestres
        estudiante.setPeriodoIngreso("2021-1");

        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(periodoConsulta);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(estudiante));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        List<Estudiante> result = useCase.getStudentsByPeriod(periodoConsulta);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSemestreFinanciero()).isEqualTo(7);
        assertThat(result.get(0).getSemestreAcademico()).isEqualTo(4);
    }

    /**
     * El semestre académico que llega desde la base de datos compartida no
     * siempre refleja la situación real. Por eso el cálculo se realiza después
     * del enriquecimiento: si el orden se invirtiera, el dato persistido
     * pisaría al calculado y el valor de matrícula sería incorrecto.
     */
    @Test
    void getStudentsByPeriod_calculatedSemesterOverridesTheOneComingFromDatabase() {
        PeriodoAcademico periodoConsulta = new PeriodoAcademico();
        periodoConsulta.setId(9L);
        periodoConsulta.setTagPeriodo(2);
        periodoConsulta.setAño(2024);

        Estudiante estudiante = new Estudiante();
        estudiante.setId(9L);
        estudiante.setCodigo("EST009");
        estudiante.setPeriodoIngreso("2020-1"); // 10 semestres reales

        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(periodoConsulta);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(estudiante));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of());
        // El enriquecimiento simula la base de datos compartida devolviendo un
        // semestre académico desactualizado.
        org.mockito.Mockito.doAnswer(inv -> {
            ((Estudiante) inv.getArgument(0)).setSemestreAcademico(2);
            return null;
        }).when(gateway).enrichPersonalData(any(Estudiante.class));

        List<Estudiante> result = useCase.getStudentsByPeriod(periodoConsulta);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSemestreAcademico()).isEqualTo(4);
        assertThat(result.get(0).getValorEnSMLV()).isEqualTo(1);
    }

    /**
     * Un valor de matrícula nulo indica que no fue posible determinar el
     * semestre financiero. Incluir esos registros trasladaría el problema al
     * reporte presupuestario en forma de totales incorrectos.
     */
    @Test
    void getStudentsByPeriod_shouldExcludeStudentsWithNullSmlv() {
        Estudiante sinSemestre = new Estudiante();
        sinSemestre.setId(7L);
        sinSemestre.setCodigo("EST007"); // sin semestreFinanciero ni periodoIngreso

        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(validPeriod);
        when(gateway.findStudentsByPeriodId(anyLong()))
                .thenReturn(List.of(studentSemester3, sinSemestre));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of());

        List<Estudiante> result = useCase.getStudentsByPeriod(validPeriod);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCodigo()).isEqualTo("EST001");
    }

    /**
     * El enriquecimiento garantiza la existencia del registro financiero del
     * estudiante para el periodo, heredando su último grupo de investigación
     * conocido.
     */
    @Test
    void enrich_shouldEnsureFinancialEnrollmentInheritingLastKnownGroup() {
        when(gateway.existsAcademicPeriod(any())).thenReturn(true);
        when(gateway.findPeriodByTagAndYear(anyInt(), anyInt())).thenReturn(validPeriod);
        when(gateway.findStudentsByPeriodId(anyLong())).thenReturn(List.of(studentSemester3));
        when(gateway.findAcademicEnrollments(anyLong(), anyInt(), anyInt())).thenReturn(List.of());
        when(gateway.findUltimoGrupoId(1L)).thenReturn(5L);

        useCase.getStudentsByPeriod(validPeriod);

        verify(gateway).registrarMatriculaFinanciera(1L, validPeriod.getId(), 5L, null);
    }

    @Test
    void getStudentByCode_whenPeriodNotProvided_shouldResolveActivePeriod() {
        PeriodoAcademico activo = new PeriodoAcademico();
        activo.setId(3L);
        activo.setTagPeriodo(2);
        activo.setAño(2025);

        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setCodigo("EST001");
        estudiante.setPeriodoIngreso("2024-1"); // 2025-2 => 4 semestres

        when(gateway.existsStudentByCode("EST001")).thenReturn(true);
        when(gateway.findStudentByCode("EST001")).thenReturn(estudiante);
        when(gateway.findActivePeriod()).thenReturn(activo);
        when(gateway.findPeriodByTagAndYear(2, 2025)).thenReturn(activo);
        when(gateway.findAcademicEnrollments(1L, 2, 2025)).thenReturn(List.of());

        Estudiante result = useCase.getStudentByCode("EST001", null, null);

        assertThat(result).isNotNull();
        assertThat(result.getSemestreFinanciero()).isEqualTo(4);
        assertThat(result.getValorEnSMLV()).isEqualTo(6);
        verify(gateway).findActivePeriod();
    }

    /**
     * Si no existe un periodo activo el estudiante se devuelve sin enriquecer,
     * evitando que la ausencia de configuración provoque un fallo del servicio.
     */
    @Test
    void getStudentByCode_whenNoActivePeriodExists_shouldReturnStudentWithoutEnriching() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setCodigo("EST001");

        when(gateway.existsStudentByCode("EST001")).thenReturn(true);
        when(gateway.findStudentByCode("EST001")).thenReturn(estudiante);
        when(gateway.findActivePeriod()).thenReturn(null);

        Estudiante result = useCase.getStudentByCode("EST001", null, null);

        assertThat(result).isSameAs(estudiante);
        assertThat(result.getValorEnSMLV()).isNull();
        verify(gateway, org.mockito.Mockito.never()).enrichPersonalData(any());
    }

    @Test
    void tieneDescuentoVoto_shouldDelegateToGateway() {
        when(gateway.tieneSolicitudCerVotoAprobada("EST001")).thenReturn(true);

        assertThat(useCase.tieneDescuentoVoto("EST001")).isTrue();
        verify(gateway).tieneSolicitudCerVotoAprobada("EST001");
    }
}

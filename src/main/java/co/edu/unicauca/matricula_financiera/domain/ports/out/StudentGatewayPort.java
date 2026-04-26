package co.edu.unicauca.matricula_financiera.domain.ports.out;

import java.time.LocalDate;
import java.util.List;

import co.edu.unicauca.matricula_financiera.domain.models.BecaDescuentoInfo;
import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;

public interface StudentGatewayPort {
    List<Estudiante> findStudentsByPeriodId(Long periodId);
    Estudiante findStudentByCode(String code);
    boolean existsStudentByCode(String code);
    void enrichPersonalData(Estudiante student);
    boolean existsAcademicPeriod(PeriodoAcademico period);
    PeriodoAcademico findPeriodByTagAndYear(Integer tag, Integer year);
    List<PeriodoAcademico> findAllPeriods();
    PeriodoAcademico findActivePeriod();
    List<MatriculaAcademica> findAcademicEnrollments(Long studentId, Integer tag, Integer year);
    boolean tieneSolicitudCerVotoAprobada(String codigoEstudiante);
    List<BecaDescuentoInfo> findBecasDescuentosByEstudianteAndPeriodo(Long estudianteId, LocalDate periodoFechaInicio, LocalDate periodoFechaFin);
    boolean findEstadoPago(Long studentId, Integer tagPeriodo, Integer anio);
    void registrarMatriculaFinanciera(Long estudianteId, Long periodoId, Long grupoId, boolean estaPago);
    Long findUltimoGrupoId(Long estudianteId);
    String findGrupoNombre(Long estudianteId, Integer tag, Integer year);
}

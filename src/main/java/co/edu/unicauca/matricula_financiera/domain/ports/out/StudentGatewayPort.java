package co.edu.unicauca.matricula_financiera.domain.ports.out;

import java.util.List;

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
}

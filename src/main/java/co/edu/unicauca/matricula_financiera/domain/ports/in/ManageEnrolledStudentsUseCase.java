package co.edu.unicauca.matricula_financiera.domain.ports.in;

import java.util.List;

import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;

public interface ManageEnrolledStudentsUseCase {
    List<Estudiante> getStudentsByPeriod(PeriodoAcademico period);
    Estudiante getStudentByCode(String code, Integer tagPeriodo, Integer year);
    List<PeriodoAcademico> getAcademicPeriods();
}

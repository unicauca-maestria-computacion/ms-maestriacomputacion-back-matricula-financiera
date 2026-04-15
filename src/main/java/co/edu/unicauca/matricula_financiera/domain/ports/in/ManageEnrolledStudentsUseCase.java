package co.edu.unicauca.matricula_financiera.domain.ports.in;

import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;

import java.util.List;

public interface ManageEnrolledStudentsUseCase {
    List<Estudiante> getStudentsByPeriod(PeriodoAcademico period);
    Estudiante getStudentByCode(String code, Integer tagPeriodo, Integer year);
    List<PeriodoAcademico> getAcademicPeriods();
}

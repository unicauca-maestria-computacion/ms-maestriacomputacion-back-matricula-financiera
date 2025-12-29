package co.edu.unicauca.matricula_financiera.aplication.input;

import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;

import java.util.List;

public interface GestionarEstudiantesMatriculadosCUIntPort {
    List<Estudiante> obtenerEstudiantes(PeriodoAcademico periodo);
    Estudiante obtenerEstudiante(Integer codigo);
    Boolean iniciarNuevaMatriculaFinanciera();
}


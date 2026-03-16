package co.edu.unicauca.matricula_financiera.aplication.output;

import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;

import java.util.List;

public interface GestionarEstudiantesMatriculadosGatewayIntPort {
    List<Estudiante> obtenerEstudiantes(PeriodoAcademico periodo);
    Estudiante obtenerEstudiante(String codigo);
    Boolean existePeriodo(PeriodoAcademico periodo);
    Boolean existeEstudiante(String codigo);
    PeriodoAcademico obtenerPeriodoAcademicoActual();
    PeriodoAcademico agregarNuevoPeriodoAcademico(PeriodoAcademico periodo);
}


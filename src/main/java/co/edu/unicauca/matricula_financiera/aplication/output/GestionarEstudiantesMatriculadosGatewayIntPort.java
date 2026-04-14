package co.edu.unicauca.matricula_financiera.aplication.output;

import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;

import java.util.List;

public interface GestionarEstudiantesMatriculadosGatewayIntPort {
    // Estudiantes
    List<Estudiante> obtenerEstudiantes(PeriodoAcademico periodo);
    List<Estudiante> obtenerEstudiantesPorIds(List<Long> ids);
    List<Estudiante> obtenerEstudiantesPorPeriodoId(Long periodoId);
    Estudiante obtenerEstudiante(String codigo);
    Boolean existeEstudiante(String codigo);

    // Datos personales desde BD compartida
    void enriquecerDatosPersonales(Estudiante estudiante);

    // Periodos desde BD compartida
    Boolean existePeriodoAcademico(PeriodoAcademico periodo);
    PeriodoAcademico findPeriodoByTagAndAnio(Integer tagPeriodo, Integer anio);
    List<PeriodoAcademico> obtenerPeriodosAcademicos();
    PeriodoAcademico obtenerPeriodoAcademicoActual();
    PeriodoAcademico agregarNuevoPeriodoAcademico(PeriodoAcademico periodo);

    // Matrículas académicas desde BD compartida
    List<MatriculaAcademica> obtenerMatriculasAcademicas(Long estudianteId, Integer tagPeriodo, Integer anio);
}

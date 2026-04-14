package co.edu.unicauca.matricula_financiera.aplication.output;

import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto.EstudianteExternoDto;

import java.util.List;
import java.util.Optional;

public interface GestionarComunicacionExternaGatewayIntPort {
    List<MatriculaAcademica> obtenerMatriculasAcademicas(PeriodoAcademico periodo);
    List<MatriculaAcademica> obtenerMatriculasAcademicasPorEstudiante(Long estudianteId, Integer tagPeriodo, Integer anio);

    /** Verifica si el periodo existe en el microservicio académico. */
    boolean existePeriodoAcademicoExterno(PeriodoAcademico periodo);

    /**
     * Obtiene todos los periodos académicos desde el microservicio académico.
     */
    List<PeriodoAcademico> obtenerPeriodosAcademicosExternos();

    /**
     * Obtiene los IDs locales de los estudiantes que tienen matrículas académicas
     * en el periodo indicado, consultando el microservicio académico.
     */
    List<Long> obtenerIdsEstudiantesPorPeriodoExterno(PeriodoAcademico periodo);

    /**
     * Obtiene nombre, apellido e identificacion de un estudiante desde el micro académico.
     * Retorna empty si no está disponible.
     */
    Optional<EstudianteExternoDto> obtenerDatosEstudianteExterno(Long estudianteId);
}

package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.gateway;

import co.edu.unicauca.matricula_financiera.aplication.output.GestionarEstudiantesMatriculadosGatewayIntPort;
import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.dominio.models.enums.PeriodoEstado;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.EstudianteEntity;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.mappers.EstudianteMapperPersistencia;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.repositories.BdCompartidaRepository;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.repositories.EstudianteReporsitoryInt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class GestionarEstudiantesMatriculadosGatewayImpAdapter
        implements GestionarEstudiantesMatriculadosGatewayIntPort {

    private final EstudianteReporsitoryInt objEstudiante;
    private final EstudianteMapperPersistencia objMapperEstudiante;
    private final BdCompartidaRepository bdCompartida;

    public GestionarEstudiantesMatriculadosGatewayImpAdapter(
            EstudianteReporsitoryInt objEstudiante,
            EstudianteMapperPersistencia objMapperEstudiante,
            BdCompartidaRepository bdCompartida) {
        this.objEstudiante = objEstudiante;
        this.objMapperEstudiante = objMapperEstudiante;
        this.bdCompartida = bdCompartida;
    }

    // -------------------------------------------------------------------------
    // Estudiantes
    // -------------------------------------------------------------------------

    @Override
    public List<Estudiante> obtenerEstudiantes(PeriodoAcademico periodo) {
        if (periodo == null) return new ArrayList<>();
        Long periodoId = resolverId(periodo);
        if (periodoId == null) return new ArrayList<>();
        return obtenerEstudiantesPorPeriodoId(periodoId);
    }

    @Override
    public List<Estudiante> obtenerEstudiantesPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(objEstudiante.findByIdIn(ids).stream()
                .map(objMapperEstudiante::mappearDeEntityAEstudiante)
                .toList());
    }

    @Override
    public List<Estudiante> obtenerEstudiantesPorPeriodoId(Long periodoId) {
        if (periodoId == null) return new ArrayList<>();
        return new ArrayList<>(objEstudiante.findByPeriodoId(periodoId).stream()
                .map(objMapperEstudiante::mappearDeEntityAEstudiante)
                .toList());
    }

    @Override
    public Estudiante obtenerEstudiante(String codigo) {
        Optional<EstudianteEntity> entity = objEstudiante.findByCodigo(codigo);
        return entity.map(objMapperEstudiante::mappearDeEntityAEstudiante).orElse(null);
    }

    @Override
    public Boolean existeEstudiante(String codigo) {
        return objEstudiante.findByCodigo(codigo).isPresent();
    }

    // -------------------------------------------------------------------------
    // Datos personales desde BD compartida (tabla personas)
    // -------------------------------------------------------------------------

    @Override
    public void enriquecerDatosPersonales(Estudiante estudiante) {
        if (estudiante == null || estudiante.getId() == null) return;
        Object[] datos = bdCompartida.findDatosPersonalesEstudiante(estudiante.getId());
        if (datos == null) return;
        estudiante.setNombre((String) datos[0]);
        estudiante.setApellido((String) datos[1]);
        Object idNum = datos[2];
        if (idNum != null) estudiante.setIdentificacion(((Number) idNum).longValue());
        Object semAcad = datos[3];
        if (semAcad != null) estudiante.setSemestreAcademico(((Number) semAcad).intValue());
    }

    // -------------------------------------------------------------------------
    // Periodos desde BD compartida
    // -------------------------------------------------------------------------

    @Override
    public Boolean existePeriodoAcademico(PeriodoAcademico periodo) {
        if (periodo == null || periodo.getTagPeriodo() == null || periodo.getAño() == null) return false;
        return bdCompartida.findPeriodoByTagAndAnio(periodo.getTagPeriodo(), periodo.getAño()) != null;
    }

    @Override
    public PeriodoAcademico findPeriodoByTagAndAnio(Integer tagPeriodo, Integer anio) {
        return bdCompartida.findPeriodoByTagAndAnio(tagPeriodo, anio);
    }

    @Override
    public List<PeriodoAcademico> obtenerPeriodosAcademicos() {
        return bdCompartida.findAllPeriodos();
    }

    @Override
    public PeriodoAcademico obtenerPeriodoAcademicoActual() {
        return bdCompartida.findAllPeriodos().stream()
                .filter(p -> p.getEstado() == PeriodoEstado.ACTIVO)
                .findFirst()
                .orElse(null);
    }

    @Override
    public PeriodoAcademico agregarNuevoPeriodoAcademico(PeriodoAcademico periodo) {
        return periodo;
    }

    // -------------------------------------------------------------------------
    // Matrículas académicas desde BD compartida
    // -------------------------------------------------------------------------

    @Override
    public List<MatriculaAcademica> obtenerMatriculasAcademicas(Long estudianteId,
                                                                 Integer tagPeriodo,
                                                                 Integer anio) {
        if (estudianteId == null || tagPeriodo == null || anio == null) return new ArrayList<>();
        return bdCompartida.findMatriculasPorEstudianteYPeriodo(estudianteId, tagPeriodo, anio);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Long resolverId(PeriodoAcademico periodo) {
        PeriodoAcademico found = bdCompartida.findPeriodoByTagAndAnio(
                periodo.getTagPeriodo(), periodo.getAño());
        return found != null ? found.getId() : null;
    }
}

package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.gateway;

import co.edu.unicauca.matricula_financiera.aplication.output.GestionarEstudiantesMatriculadosGatewayIntPort;
import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.EstudianteEntity;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.PeriodoAcademicoEntity;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.mappers.EstudianteMapperPersistencia;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.mappers.PeriodoAcademicoMapperPersistencia;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.repositories.EstudianteReporsitoryInt;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.repositories.PeriodoAcademicoReporsitoryInt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GestionarEstudiantesMatriculadosGatewayImpAdapter
        implements GestionarEstudiantesMatriculadosGatewayIntPort {

    private final EstudianteReporsitoryInt objEstudiante;
    private final PeriodoAcademicoReporsitoryInt objPeriodoAcademico;
    private final EstudianteMapperPersistencia objMapperEstudiante;
    private final PeriodoAcademicoMapperPersistencia objMapperPeriodoAcademico;

    public GestionarEstudiantesMatriculadosGatewayImpAdapter(
            EstudianteReporsitoryInt objEstudiante,
            PeriodoAcademicoReporsitoryInt objPeriodoAcademico,
            EstudianteMapperPersistencia objMapperEstudiante,
            PeriodoAcademicoMapperPersistencia objMapperPeriodoAcademico) {
        this.objEstudiante = objEstudiante;
        this.objPeriodoAcademico = objPeriodoAcademico;
        this.objMapperEstudiante = objMapperEstudiante;
        this.objMapperPeriodoAcademico = objMapperPeriodoAcademico;
    }

    @Override
    public List<Estudiante> obtenerEstudiantes(PeriodoAcademico periodo) {
        List<EstudianteEntity> estudiantesEntity = objEstudiante.findByPeriodoAcademico(
                periodo.getPeriodo(), periodo.getAño());
        return estudiantesEntity.stream()
                .map(objMapperEstudiante::mappearDeEntityAEstudiante)
                .toList();
    }

    @Override
    public Estudiante obtenerEstudiante(Integer codigo) {
        Optional<EstudianteEntity> estudianteEntity = objEstudiante.findByCodigo(codigo);
        return estudianteEntity.map(objMapperEstudiante::mappearDeEntityAEstudiante).orElse(null);
    }

    @Override
    public Boolean existePeriodo(PeriodoAcademico periodo) {
        return objPeriodoAcademico.findByPeriodoAndAño(periodo.getPeriodo(), periodo.getAño()).isPresent();
    }

    @Override
    public Boolean existeEstudiante(Integer codigo) {
        return objEstudiante.findByCodigo(codigo).isPresent();
    }

    @Override
    public PeriodoAcademico obtenerPeriodoAcademicoActual() {
        Optional<PeriodoAcademicoEntity> periodoEntity = objPeriodoAcademico.findTopByOrderByAñoDescPeriodoDesc();
        return periodoEntity.map(objMapperPeriodoAcademico::mappearDeEntityAPeriodoAcademico).orElse(null);
    }

    @Override
    public PeriodoAcademico agregarNuevoPeriodoAcademico(PeriodoAcademico periodo) {
        PeriodoAcademicoEntity periodoEntity = objMapperPeriodoAcademico.mappearPeriodoAcademicoAEntity(periodo);
        PeriodoAcademicoEntity savedEntity = objPeriodoAcademico.save(periodoEntity);
        return objMapperPeriodoAcademico.mappearDeEntityAPeriodoAcademico(savedEntity);
    }
}

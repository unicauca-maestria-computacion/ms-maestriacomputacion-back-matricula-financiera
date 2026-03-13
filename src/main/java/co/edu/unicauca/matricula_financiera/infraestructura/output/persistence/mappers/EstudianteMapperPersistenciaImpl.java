package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.EstudianteEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstudianteMapperPersistenciaImpl implements EstudianteMapperPersistencia {

    @Override
    public Estudiante mappearDeEntityAEstudiante(EstudianteEntity estudiante) {
        if (estudiante == null) {
            return null;
        }
        Estudiante domain = new Estudiante();
        domain.setCodigo(estudiante.getCodigo());
        domain.setCohorte(estudiante.getCohorte());
        domain.setPeriodoIngreso(estudiante.getPeriodoIngreso());
        domain.setSemestreFinanciero(estudiante.getSemestreFinanciero());
        domain.setMatriculasFinancieras(Collections.emptyList());
        domain.setDescuentos(Collections.emptyList());
        domain.setBecas(Collections.emptyList());
        domain.setMatriculasAcademicas(Collections.emptyList());
        return domain;
    }

    @Override
    public List<Estudiante> mappearDeListaEntityAEstudiante(List<EstudianteEntity> estudiantes) {
        if (estudiantes == null) {
            return Collections.emptyList();
        }
        return estudiantes.stream()
                .map(this::mappearDeEntityAEstudiante)
                .collect(Collectors.toList());
    }

    @Override
    public EstudianteEntity mappearEstudianteAEntity(Estudiante estudiante) {
        if (estudiante == null) {
            return null;
        }
        EstudianteEntity entity = new EstudianteEntity();
        entity.setCodigo(estudiante.getCodigo());
        entity.setCohorte(estudiante.getCohorte());
        entity.setPeriodoIngreso(estudiante.getPeriodoIngreso());
        entity.setSemestreFinanciero(estudiante.getSemestreFinanciero());
        entity.setMatriculasFinancieras(null);
        entity.setDescuentos(null);
        entity.setBecas(null);
        return entity;
    }
}

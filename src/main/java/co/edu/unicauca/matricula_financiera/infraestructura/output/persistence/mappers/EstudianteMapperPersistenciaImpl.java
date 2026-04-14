package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.EstudianteEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EstudianteMapperPersistenciaImpl implements EstudianteMapperPersistencia {

    @Override
    public Estudiante mappearDeEntityAEstudiante(EstudianteEntity entity) {
        if (entity == null) return null;

        Estudiante domain = new Estudiante();
        domain.setId(entity.getId());
        domain.setCodigo(entity.getCodigo());
        domain.setCohorte(entity.getCohorte());
        domain.setPeriodoIngreso(entity.getPeriodoIngreso());
        domain.setSemestreFinanciero(entity.getSemestreFinanciero());
        domain.setSemestreAcademico(entity.getSemestreAcademico());

        // nombre, apellido, identificacion se enriquecen desde el micro académico
        domain.setMatriculasFinancieras(new ArrayList<>());
        domain.setDescuentos(new ArrayList<>());
        domain.setBecas(new ArrayList<>());
        domain.setMatriculasAcademicas(new ArrayList<>());
        return domain;
    }

    @Override
    public List<Estudiante> mappearDeListaEntityAEstudiante(List<EstudianteEntity> estudiantes) {
        if (estudiantes == null) return new ArrayList<>();
        return estudiantes.stream()
                .map(this::mappearDeEntityAEstudiante)
                .collect(Collectors.toList());
    }

    @Override
    public EstudianteEntity mappearEstudianteAEntity(Estudiante estudiante) {
        if (estudiante == null) return null;
        EstudianteEntity entity = new EstudianteEntity();
        entity.setCodigo(estudiante.getCodigo());
        entity.setPeriodoIngreso(estudiante.getPeriodoIngreso());
        entity.setSemestreFinanciero(estudiante.getSemestreFinanciero());
        return entity;
    }
}

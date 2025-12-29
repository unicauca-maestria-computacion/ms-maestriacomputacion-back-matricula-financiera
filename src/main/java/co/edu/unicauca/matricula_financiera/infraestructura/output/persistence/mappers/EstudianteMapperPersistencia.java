package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.EstudianteEntity;

import java.util.List;

public interface EstudianteMapperPersistencia {
    Estudiante mappearDeEntityAEstudiante(EstudianteEntity estudiante);
    List<Estudiante> mappearDeListaEntityAEstudiante(List<EstudianteEntity> estudiantes);
    EstudianteEntity mappearEstudianteAEntity(Estudiante estudiante);
}


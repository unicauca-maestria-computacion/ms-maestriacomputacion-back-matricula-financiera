package co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.mapper;

import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.entity.EstudianteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EstudiantePersistenceMapper {
    Estudiante fromEntityToEstudiante(EstudianteEntity entity);
    List<Estudiante> fromListEntityToEstudiante(List<EstudianteEntity> entities);
    EstudianteEntity fromEstudianteToEntity(Estudiante estudiante);
}

package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.repositories;

import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteReporsitoryInt extends JpaRepository<EstudianteEntity, Long> {
    Optional<EstudianteEntity> findByCodigo(Integer codigo);
}


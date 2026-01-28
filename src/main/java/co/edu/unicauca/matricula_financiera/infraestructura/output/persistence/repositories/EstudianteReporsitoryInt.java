package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.repositories;

import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteReporsitoryInt extends JpaRepository<EstudianteEntity, Long> {
    Optional<EstudianteEntity> findByCodigo(Integer codigo);

    @Query("SELECT DISTINCT e FROM EstudianteEntity e " +
           "JOIN e.matriculasFinancieras mf " +
           "JOIN mf.objPeriodoAcademico p " +
           "WHERE p.periodo = :periodo AND p.año = :año")
    List<EstudianteEntity> findByPeriodoAcademico(@Param("periodo") Integer periodo, @Param("año") Integer año);
}


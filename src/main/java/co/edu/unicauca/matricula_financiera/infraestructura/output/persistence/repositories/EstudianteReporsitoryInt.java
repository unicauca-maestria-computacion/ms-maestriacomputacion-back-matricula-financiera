package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.repositories;

import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteReporsitoryInt extends JpaRepository<EstudianteEntity, Integer> {

    Optional<EstudianteEntity> findByCodigo(String codigo);

    List<EstudianteEntity> findByIdIn(List<Long> ids);

    /**
     * Estudiantes matriculados en un periodo académico específico.
     * Accede directamente a las tablas matriculas y cursos de la BD compartida.
     */
    @Query(value = """
            SELECT DISTINCT e.* FROM estudiantes e
            JOIN matriculas m ON m.id_estudiante = e.id
            JOIN cursos c     ON m.id_curso = c.id
            WHERE c.periodo_id = :periodoId
              AND m.estado = 1
            """, nativeQuery = true)
    List<EstudianteEntity> findByPeriodoId(@Param("periodoId") Long periodoId);
}

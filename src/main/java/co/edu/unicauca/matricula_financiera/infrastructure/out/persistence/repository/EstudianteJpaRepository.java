package co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.repository;

import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.entity.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteJpaRepository extends JpaRepository<EstudianteEntity, Long> {

    Optional<EstudianteEntity> findByCodigo(String codigo);

    @Query(value = """
            SELECT DISTINCT e.* FROM estudiantes e
            JOIN matriculas m ON m.id_estudiante = e.id
            JOIN cursos c     ON m.id_curso = c.id
            WHERE c.periodo_id = :periodoId
              AND m.estado = 1
            """, nativeQuery = true)
    List<EstudianteEntity> findByPeriodoId(@Param("periodoId") Long periodoId);
}

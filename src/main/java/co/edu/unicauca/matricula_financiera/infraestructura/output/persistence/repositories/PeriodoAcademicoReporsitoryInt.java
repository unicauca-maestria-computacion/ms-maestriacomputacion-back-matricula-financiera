package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.repositories;

import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.PeriodoAcademicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeriodoAcademicoReporsitoryInt extends JpaRepository<PeriodoAcademicoEntity, Long> {
    Optional<PeriodoAcademicoEntity> findByPeriodoAndAño(Integer periodo, Integer año);

    Optional<PeriodoAcademicoEntity> findTopByOrderByAñoDescPeriodoDesc();
}


package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.PeriodoAcademicoEntity;

public interface PeriodoAcademicoMapperPersistencia {
    PeriodoAcademico mappearDeEntityAPeriodoAcademico(PeriodoAcademicoEntity periodo);
    PeriodoAcademicoEntity mappearPeriodoAcademicoAEntity(PeriodoAcademico periodo);
}


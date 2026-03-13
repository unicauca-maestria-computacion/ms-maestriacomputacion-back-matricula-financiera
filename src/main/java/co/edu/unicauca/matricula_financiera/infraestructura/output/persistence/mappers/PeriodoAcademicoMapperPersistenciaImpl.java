package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities.PeriodoAcademicoEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PeriodoAcademicoMapperPersistenciaImpl implements PeriodoAcademicoMapperPersistencia {

    @Override
    public PeriodoAcademico mappearDeEntityAPeriodoAcademico(PeriodoAcademicoEntity periodo) {
        if (periodo == null) {
            return null;
        }
        PeriodoAcademico domain = new PeriodoAcademico();
        domain.setPeriodo(periodo.getPeriodo());
        domain.setAño(periodo.getAño());
        domain.setMatriculasFinancieras(Collections.emptyList());
        return domain;
    }

    @Override
    public PeriodoAcademicoEntity mappearPeriodoAcademicoAEntity(PeriodoAcademico periodo) {
        if (periodo == null) {
            return null;
        }
        PeriodoAcademicoEntity entity = new PeriodoAcademicoEntity();
        entity.setPeriodo(periodo.getPeriodo());
        entity.setAño(periodo.getAño());
        entity.setMatriculasFinancieras(null);
        return entity;
    }
}

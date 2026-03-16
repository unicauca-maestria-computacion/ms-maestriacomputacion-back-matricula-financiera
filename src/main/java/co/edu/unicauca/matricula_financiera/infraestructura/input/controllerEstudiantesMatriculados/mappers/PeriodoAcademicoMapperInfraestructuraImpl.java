package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOPeticion.PeriodoAcademicoDTOPeticion;
import org.springframework.stereotype.Component;

@Component
public class PeriodoAcademicoMapperInfraestructuraImpl implements PeriodoAcademicoMapperInfraestructura {

    @Override
    public PeriodoAcademico mappearDePeticionAPeriodoAcademico(PeriodoAcademicoDTOPeticion periodo) {
        if (periodo == null) {
            return null;
        }
        PeriodoAcademico result = new PeriodoAcademico();
        result.setPeriodo(periodo.getPeriodo());
        result.setAño(periodo.getAño());
        return result;
    }
}

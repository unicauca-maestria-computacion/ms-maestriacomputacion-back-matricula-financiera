package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOPeticion.PeriodoAcademicoDTOPeticion;

public interface PeriodoAcademicoMapperInfraestructura {
    PeriodoAcademico mappearDePeticionAPeriodoAcademico(PeriodoAcademicoDTOPeticion periodo);
}


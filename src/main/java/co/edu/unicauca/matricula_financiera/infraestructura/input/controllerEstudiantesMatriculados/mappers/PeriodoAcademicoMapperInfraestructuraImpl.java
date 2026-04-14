package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.dominio.models.enums.PeriodoEstado;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.PeriodoAcademicoDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOPeticion.PeriodoAcademicoDTOPeticion;
import org.springframework.stereotype.Component;

@Component
public class PeriodoAcademicoMapperInfraestructuraImpl implements PeriodoAcademicoMapperInfraestructura {

    @Override
    public PeriodoAcademico mappearDePeticionAPeriodoAcademico(PeriodoAcademicoDTOPeticion peticion) {
        if (peticion == null) return null;
        PeriodoAcademico result = new PeriodoAcademico();
        // tagPeriodo tiene prioridad; si no viene, usar periodo como fallback
        Integer tag = peticion.getTagPeriodo() != null ? peticion.getTagPeriodo() : peticion.getPeriodo();
        result.setTagPeriodo(tag);
        result.setAño(peticion.getAño());
        result.setFechaInicio(peticion.getFechaInicio());
        result.setFechaFin(peticion.getFechaFin());
        result.setFechaFinMatricula(peticion.getFechaFinMatricula());
        result.setDescripcion(peticion.getDescripcion());
        result.setEstado(parsearEstado(peticion.getEstado()));
        return result;
    }

    @Override
    public PeriodoAcademicoDTORespuesta mappearDePeriodoAcademicoARespuesta(PeriodoAcademico periodo) {
        if (periodo == null) return null;
        PeriodoAcademicoDTORespuesta dto = new PeriodoAcademicoDTORespuesta();
        dto.setId(periodo.getId());
        dto.setTagPeriodo(periodo.getTagPeriodo());
        dto.setPeriodo(periodo.getPeriodo());       // derivado: igual a tagPeriodo
        dto.setAño(periodo.getAño());               // derivado: año de fechaInicio
        dto.setFechaInicio(periodo.getFechaInicio());
        dto.setFechaFin(periodo.getFechaFin());
        dto.setFechaFinMatricula(periodo.getFechaFinMatricula());
        dto.setDescripcion(periodo.getDescripcion());
        dto.setEstado(periodo.getEstado() != null ? periodo.getEstado().name() : null);
        return dto;
    }

    private PeriodoEstado parsearEstado(String estado) {
        if (estado == null || estado.isBlank()) return PeriodoEstado.ACTIVO;
        try {
            return PeriodoEstado.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            return PeriodoEstado.ACTIVO;
        }
    }
}

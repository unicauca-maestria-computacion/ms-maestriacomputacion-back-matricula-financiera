package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.mapper;

import co.edu.unicauca.matricula_financiera.domain.models.BecaDescuentoInfo;
import co.edu.unicauca.matricula_financiera.domain.models.Docente;
import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.Materia;
import co.edu.unicauca.matricula_financiera.domain.models.MatriculaAcademica;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoRequest.PeriodoAcademicoRequest;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.BecaDescuentoInfoResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.DocenteResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.MateriaResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.PeriodoAcademicoResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentHttpMapper {

    @Mapping(target = "año", source = "anio")
    @Mapping(target = "tagPeriodo", source = "tagPeriodo")
    PeriodoAcademico fromRequestToPeriodo(PeriodoAcademicoRequest request);

    @Mapping(target = "anio", source = "año")
    PeriodoAcademicoResponse fromPeriodoToResponse(PeriodoAcademico periodo);

    List<PeriodoAcademicoResponse> fromListPeriodosToResponse(List<PeriodoAcademico> periodos);

    @Mapping(target = "materias", expression = "java(flatMaterias(estudiante))")
    @Mapping(target = "becasDescuentos", expression = "java(mapBecasDescuentos(estudiante))")
    StudentResponse fromEstudianteToResponse(Estudiante estudiante);

    List<StudentResponse> fromListToResponse(List<Estudiante> estudiantes);

    BecaDescuentoInfoResponse fromBecaDescuentoInfoToResponse(BecaDescuentoInfo info);

    @Mapping(target = "codigoOid", source = "codigo_oid")
    @Mapping(target = "docente", source = "objDocente")
    MateriaResponse fromMateriaToResponse(Materia materia);

    DocenteResponse fromDocenteToResponse(Docente docente);

    default List<MateriaResponse> flatMaterias(Estudiante estudiante) {
        if (estudiante.getMatriculasAcademicas() == null) return new ArrayList<>();
        List<MateriaResponse> result = new ArrayList<>();
        for (MatriculaAcademica ma : estudiante.getMatriculasAcademicas()) {
            if (ma.getMaterias() != null) {
                for (Materia m : ma.getMaterias()) {
                    result.add(fromMateriaToResponse(m));
                }
            }
        }
        return result;
    }

    default List<BecaDescuentoInfoResponse> mapBecasDescuentos(Estudiante estudiante) {
        if (estudiante.getBecasDescuentos() == null) return new ArrayList<>();
        return estudiante.getBecasDescuentos().stream()
                .map(this::fromBecaDescuentoInfoToResponse)
                .collect(java.util.stream.Collectors.toList());
    }
}

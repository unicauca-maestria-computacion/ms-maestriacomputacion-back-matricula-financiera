package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.controller;

import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.domain.ports.in.ManageEnrolledStudentsUseCase;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoRequest.PeriodoAcademicoRequest;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.PeriodoAcademicoResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.StudentResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.mapper.StudentHttpMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gestion-matricula-financiera")
@RequiredArgsConstructor
@Validated
public class StudentController {

    private final ManageEnrolledStudentsUseCase useCase;
    private final StudentHttpMapper mapper;

    @PostMapping("/estudiantes")
    public ResponseEntity<List<StudentResponse>> getStudentsByPeriod(
            @Valid @RequestBody PeriodoAcademicoRequest request) {
        PeriodoAcademico period = mapper.fromRequestToPeriodo(request);
        List<Estudiante> students = useCase.getStudentsByPeriod(period);
        return ResponseEntity.ok(mapper.fromListToResponse(students));
    }

    @GetMapping("/estudiantes/{codigo}")
    public ResponseEntity<StudentResponse> getStudentByCode(
            @PathVariable String codigo,
            @RequestParam(required = false) Integer tagPeriodo,
            @RequestParam(required = false) Integer anio) {
        Estudiante student = useCase.getStudentByCode(codigo, tagPeriodo, anio);
        return ResponseEntity.ok(mapper.fromEstudianteToResponse(student));
    }

    @GetMapping("/estudiantes/{codigo}/descuento-voto")
    public ResponseEntity<Boolean> tieneDescuentoVoto(@PathVariable String codigo) {
        boolean tiene = useCase.tieneDescuentoVoto(codigo);
        return ResponseEntity.ok(tiene);
    }

    @GetMapping("/periodos")
    public ResponseEntity<List<PeriodoAcademicoResponse>> getAcademicPeriods() {
        List<PeriodoAcademico> periods = useCase.getAcademicPeriods();
        return ResponseEntity.ok(mapper.fromListPeriodosToResponse(periods));
    }
}

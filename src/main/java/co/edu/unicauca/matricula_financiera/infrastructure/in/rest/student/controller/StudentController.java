package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.controller;

import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.domain.ports.in.ManageEnrolledStudentsUseCase;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoRequest.PeriodoAcademicoRequest;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.PeriodoAcademicoResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.ReporteCentroPostgradosResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.StudentResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.mapper.StudentHttpMapper;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.repository.BdCompartidaRepository;
import co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.repository.ReporteCentroPostgradosRow;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/gestion-matricula-financiera")
@RequiredArgsConstructor
@Validated
public class StudentController {

    private final ManageEnrolledStudentsUseCase useCase;
    private final StudentHttpMapper mapper;
    private final BdCompartidaRepository bdCompartidaRepository;

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

    @PostMapping("/iniciar")
    public ResponseEntity<Boolean> iniciarNuevaMatriculaFinanciera() {
        return ResponseEntity.ok(Boolean.TRUE);
    }

    @GetMapping("/reporte-centro-postgrados/{periodoId}")
    public ResponseEntity<List<ReporteCentroPostgradosResponse>> getReporteCentroPostgrados(@PathVariable Long periodoId) {
        List<ReporteCentroPostgradosRow> rows = bdCompartidaRepository.findReporteCentroPostgrados(periodoId);

        // Agrupar por estudiante para consolidar materias
        Map<String, ReporteCentroPostgradosResponse> map = new LinkedHashMap<>();
        for (ReporteCentroPostgradosRow row : rows) {
            String key = row.getIdentificacion();
            ReporteCentroPostgradosResponse r = map.get(key);
            if (r == null) {
                r = new ReporteCentroPostgradosResponse();
                r.setIdentificacion(row.getIdentificacion());
                r.setNombreCompleto(row.getNombreCompleto());
                r.setValorMatriculaSMMLV(row.getValorMatriculaSMMLV());
                r.setSemestreFinanciero(row.getSemestreFinanciero());
                r.setAplicaDescuentoVoto(row.isAplicaDescuentoVoto());
                r.setAplicaDescuentoEgresado(row.isAplicaDescuentoEgresado());
                r.setResolucionBeca(row.getResolucionBeca());
                r.setPorcentajeBeca(row.getPorcentajeBeca());
                r.setSemestreAcademico(row.getSemestreAcademico());
                r.setDocenteEncargado(row.getDocente());
                r.setGrupoClase(row.getGrupo());
                r.setMaterias(new ArrayList<>());
                map.put(key, r);
            }
            if (row.getMateria() != null && !row.getMateria().isEmpty()) {
                ReporteCentroPostgradosResponse.MateriaReporte mr = new ReporteCentroPostgradosResponse.MateriaReporte(
                    row.getCodigoOid(), row.getMateria()
                );
                boolean exists = r.getMaterias().stream()
                    .anyMatch(m -> mr.getMateria().equals(m.getMateria()));
                if (!exists) {
                    r.getMaterias().add(mr);
                }
            }
            // Si hay un docente en otra fila, usar ese
            if (r.getDocenteEncargado() == null || r.getDocenteEncargado().isEmpty()) {
                r.setDocenteEncargado(row.getDocente());
                r.setGrupoClase(row.getGrupo());
            }
        }
        return ResponseEntity.ok(new ArrayList<>(map.values()));
    }
}

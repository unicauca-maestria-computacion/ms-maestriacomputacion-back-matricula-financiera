package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.controller;

import co.edu.unicauca.matricula_financiera.domain.models.Estudiante;
import co.edu.unicauca.matricula_financiera.domain.models.PeriodoAcademico;
import co.edu.unicauca.matricula_financiera.domain.ports.in.ManageEnrolledStudentsUseCase;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoRequest.PeriodoAcademicoRequest;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.PeriodoAcademicoResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse.StudentResponse;
import co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.mapper.StudentHttpMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Matricula Financiera",
     description = "Consulta de estudiantes matriculados, calculo del valor de matricula "
                 + "en SMLV y periodos academicos")
public class StudentController {

    private final ManageEnrolledStudentsUseCase useCase;
    private final StudentHttpMapper mapper;

    @Operation(
            summary = "Consultar los estudiantes matriculados en un periodo academico",
            description = """
                    Retorna los estudiantes con matricula academica activa en el periodo indicado.
                    Para cada estudiante se completan los datos personales, las asignaturas
                    matriculadas con su docente, las becas y descuentos vigentes, el estado de pago
                    y el grupo de investigacion; a continuacion se calcula el semestre financiero a
                    partir del periodo de ingreso y, con el, el valor de matricula en SMLV.

                    Los estudiantes cuyo valor calculado resulta nulo se excluyen de la respuesta,
                    ya que un valor nulo indica que no fue posible determinar el semestre financiero
                    y su inclusion trasladaria totales incorrectos al reporte presupuestario.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Lista de estudiantes matriculados con su valor de matricula"),
            @ApiResponse(responseCode = "400",
                    description = "El cuerpo de la peticion es invalido (codigo MF-0006)",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(responseCode = "404",
                    description = "El periodo academico indicado no existe (codigo MF-0003)",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class))),
            @ApiResponse(responseCode = "422",
                    description = "El periodo academico es nulo (codigo MF-0005)",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    @PostMapping("/estudiantes")
    public ResponseEntity<List<StudentResponse>> getStudentsByPeriod(
            @Valid @RequestBody PeriodoAcademicoRequest request) {
        PeriodoAcademico period = mapper.fromRequestToPeriodo(request);
        List<Estudiante> students = useCase.getStudentsByPeriod(period);
        return ResponseEntity.ok(mapper.fromListToResponse(students));
    }

    @Operation(
            summary = "Consultar un estudiante por su codigo",
            description = """
                    Retorna la informacion de matricula financiera de un unico estudiante.
                    Los parametros de periodo son opcionales: cuando no se suministran, el
                    sistema recupera el periodo activo y lo utiliza como referencia. Si tampoco
                    existe un periodo activo, el estudiante se devuelve sin enriquecer, evitando
                    que la ausencia de configuracion provoque un fallo del servicio.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
            @ApiResponse(responseCode = "404",
                    description = "No existe un estudiante con ese codigo (codigo MF-0003)",
                    content = @Content(schema = @Schema(implementation = org.springframework.http.ProblemDetail.class)))
    })
    @GetMapping("/estudiantes/{codigo}")
    public ResponseEntity<StudentResponse> getStudentByCode(
            @Parameter(description = "Codigo institucional del estudiante", example = "EST001")
            @PathVariable String codigo,
            @Parameter(description = "Semestre del periodo academico (1 o 2). Opcional", example = "1")
            @RequestParam(required = false) Integer tagPeriodo,
            @Parameter(description = "Anio del periodo academico. Opcional", example = "2024")
            @RequestParam(required = false) Integer anio) {
        Estudiante student = useCase.getStudentByCode(codigo, tagPeriodo, anio);
        return ResponseEntity.ok(mapper.fromEstudianteToResponse(student));
    }

    @Operation(
            summary = "Verificar el descuento por certificado de votacion",
            description = """
                    Indica si el estudiante cuenta con una solicitud de tipo CER_VOTO en estado
                    aprobado. Se expone de forma independiente porque el Front-End la utiliza para
                    mostrar el indicador correspondiente sin necesidad de recuperar el registro
                    completo del estudiante.""")
    @ApiResponse(responseCode = "200", description = "true si el descuento aplica, false en caso contrario")
    @GetMapping("/estudiantes/{codigo}/descuento-voto")
    public ResponseEntity<Boolean> tieneDescuentoVoto(
            @Parameter(description = "Codigo institucional del estudiante", example = "EST001")
            @PathVariable String codigo) {
        boolean tiene = useCase.tieneDescuentoVoto(codigo);
        return ResponseEntity.ok(tiene);
    }

    @Operation(
            summary = "Listar los periodos academicos registrados",
            description = "Retorna todos los periodos academicos ordenados por fecha de inicio "
                        + "descendente, con su estado (ACTIVO, INACTIVO o CERRADO).")
    @ApiResponse(responseCode = "200", description = "Lista de periodos academicos")
    @GetMapping("/periodos")
    public ResponseEntity<List<PeriodoAcademicoResponse>> getAcademicPeriods() {
        List<PeriodoAcademico> periods = useCase.getAcademicPeriods();
        return ResponseEntity.ok(mapper.fromListPeriodosToResponse(periods));
    }

    @Operation(
            summary = "Iniciar un nuevo proceso de matricula financiera",
            description = "Punto de entrada reservado para el inicio del proceso de matricula. "
                        + "En la implementacion actual confirma la disponibilidad del servicio.")
    @ApiResponse(responseCode = "200", description = "Confirmacion de la operacion")
    @PostMapping("/iniciar")
    public ResponseEntity<Boolean> iniciarNuevaMatriculaFinanciera() {
        return ResponseEntity.ok(Boolean.TRUE);
    }
}

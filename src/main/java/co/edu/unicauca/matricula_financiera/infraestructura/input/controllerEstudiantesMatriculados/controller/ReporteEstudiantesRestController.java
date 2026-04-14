package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.controller;

import co.edu.unicauca.matricula_financiera.aplication.input.GestionarEstudiantesMatriculadosCUIntPort;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.EstudianteDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.PeriodoAcademicoDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOPeticion.PeriodoAcademicoDTOPeticion;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers.EstudianteMapperInfraestructura;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers.PeriodoAcademicoMapperInfraestructura;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gestion-matricula-financiera")
public class ReporteEstudiantesRestController {

    private final GestionarEstudiantesMatriculadosCUIntPort objGestionarEstudiantesMatriculadosCUInt;
    private final EstudianteMapperInfraestructura objMapperEstudiante;
    private final PeriodoAcademicoMapperInfraestructura objMapperPeriodoAcademico;

    public ReporteEstudiantesRestController(
            GestionarEstudiantesMatriculadosCUIntPort objGestionarEstudiantesMatriculadosCUInt,
            EstudianteMapperInfraestructura objMapperEstudiante,
            PeriodoAcademicoMapperInfraestructura objMapperPeriodoAcademico) {
        this.objGestionarEstudiantesMatriculadosCUInt = objGestionarEstudiantesMatriculadosCUInt;
        this.objMapperEstudiante = objMapperEstudiante;
        this.objMapperPeriodoAcademico = objMapperPeriodoAcademico;
    }

    @PostMapping("/obtener-estudiantes")
    public ResponseEntity<List<EstudianteDTORespuesta>> obtenerEstudiantes(
            @RequestBody PeriodoAcademicoDTOPeticion periodo) {
        var periodoAcademico = objMapperPeriodoAcademico.mappearDePeticionAPeriodoAcademico(periodo);
        var estudiantes = objGestionarEstudiantesMatriculadosCUInt.obtenerEstudiantes(periodoAcademico);
        var estudiantesDTO = objMapperEstudiante.mappearDeListaEstudianteARespuesta(estudiantes);
        return new ResponseEntity<>(estudiantesDTO, HttpStatus.OK);
    }

    @GetMapping("/obtener-estudiante/{codigo}")
    public ResponseEntity<EstudianteDTORespuesta> obtenerEstudiante(
            @PathVariable("codigo") String codigo,
            @RequestParam(required = false) Integer tagPeriodo,
            @RequestParam(required = false) Integer anio) {
        var estudiante = objGestionarEstudiantesMatriculadosCUInt.obtenerEstudiante(codigo, tagPeriodo, anio);
        if (estudiante == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        var estudianteDTO = objMapperEstudiante.mappearDeEstudianteARespuesta(estudiante);
        return new ResponseEntity<>(estudianteDTO, HttpStatus.OK);
    }

    @GetMapping({"/periodos-academicos", "/periodos-financieros"})
    public ResponseEntity<List<PeriodoAcademicoDTORespuesta>> obtenerPeriodosAcademicos() {
        var periodos = objGestionarEstudiantesMatriculadosCUInt.obtenerPeriodosAcademicos();
        var periodosDTO = periodos.stream()
                .map(objMapperPeriodoAcademico::mappearDePeriodoAcademicoARespuesta)
                .toList();
        return new ResponseEntity<>(periodosDTO, HttpStatus.OK);
    }
}

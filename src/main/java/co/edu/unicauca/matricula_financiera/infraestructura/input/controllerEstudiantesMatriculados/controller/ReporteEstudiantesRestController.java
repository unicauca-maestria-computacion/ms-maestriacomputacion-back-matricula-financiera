package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.controller;

import co.edu.unicauca.matricula_financiera.aplication.input.GestionarEstudiantesMatriculadosCUIntPort;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.EstudianteDTORespuesta;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOPeticion.PeriodoAcademicoDTOPeticion;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers.EstudianteMapperInfraestructura;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers.PeriodoAcademicoMapperInfraestructura;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
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
    
    @GetMapping("/periodo")
    public ResponseEntity<List<EstudianteDTORespuesta>> obtenerEstudiantes(
            @RequestBody PeriodoAcademicoDTOPeticion periodo) {
        var periodoAcademico = objMapperPeriodoAcademico.mappearDePeticionAPeriodoAcademico(periodo);
        var estudiantes = objGestionarEstudiantesMatriculadosCUInt.obtenerEstudiantes(periodoAcademico);
        var estudiantesDTO = objMapperEstudiante.mappearDeListaEstudianteARespuesta(estudiantes);
        return new ResponseEntity<>(estudiantesDTO, HttpStatus.OK);
    }
    
    @GetMapping("/{codigo}")
    public ResponseEntity<EstudianteDTORespuesta> obtenerEstudiante(@PathVariable Integer codigo) {
        var estudiante = objGestionarEstudiantesMatriculadosCUInt.obtenerEstudiante(codigo);
        if (estudiante == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var estudianteDTO = objMapperEstudiante.mappearDeEstudianteARespuesta(estudiante);
        return new ResponseEntity<>(estudianteDTO, HttpStatus.OK);
    }
    
    @PostMapping("/iniciar-matricula")
    public ResponseEntity<Boolean> iniciarNuevaMatriculaFinanciera() {
        Boolean resultado = objGestionarEstudiantesMatriculadosCUInt.iniciarNuevaMatriculaFinanciera();
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }
}


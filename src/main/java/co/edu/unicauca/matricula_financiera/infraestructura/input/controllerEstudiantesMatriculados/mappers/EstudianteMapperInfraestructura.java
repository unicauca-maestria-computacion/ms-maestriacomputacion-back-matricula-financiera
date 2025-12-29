package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.mappers;

import co.edu.unicauca.matricula_financiera.dominio.models.Estudiante;
import co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer.EstudianteDTORespuesta;

import java.util.List;

public interface EstudianteMapperInfraestructura {
    EstudianteDTORespuesta mappearDeEstudianteARespuesta(Estudiante estudiante);
    List<EstudianteDTORespuesta> mappearDeListaEstudianteARespuesta(List<Estudiante> estudiantes);
}


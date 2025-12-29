package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaAcademicaDTORespuesta {
    private Integer semestre;
    private List<MateriaDTORespuesta> materias;
    private PeriodoAcademicoDTORespuesta objPeriodoAcademico;
}

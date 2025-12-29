package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaFinancieraDTORespuesta {
    private Date fechaMatricula;
    private Float valorMatricula;
    private Boolean pagada;
    private PeriodoAcademicoDTORespuesta objPeriodoAcademico;
}


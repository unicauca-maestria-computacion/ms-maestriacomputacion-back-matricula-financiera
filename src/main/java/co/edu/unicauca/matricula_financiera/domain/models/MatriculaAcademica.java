package co.edu.unicauca.matricula_financiera.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaAcademica {
    private Integer semestre;
    private List<Materia> materias;
    private List<Estudiante> estudiantes;
    private PeriodoAcademico objPeriodoAcademico;
}

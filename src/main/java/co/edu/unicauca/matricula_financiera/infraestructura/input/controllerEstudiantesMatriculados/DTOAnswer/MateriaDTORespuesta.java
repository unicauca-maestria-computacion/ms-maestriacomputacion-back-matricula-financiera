package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MateriaDTORespuesta {
    private String codigo_oid;
    private Integer semestreAcademico;
    private String materia;
    private DocenteDTORespuesta objDocente;
    private String grupoClase;
}


package co.edu.unicauca.matricula_financiera.dominio.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Materia {
    private String codigo_oid;
    private Integer semestreAcademico;
    private String materia;
    private Docente objDocente;
    private String grupoClase;
    private MatriculaAcademica objMatriculaAcademica;
}


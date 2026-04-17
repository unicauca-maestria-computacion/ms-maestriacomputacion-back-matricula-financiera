package co.edu.unicauca.matricula_financiera.domain.models;

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
    private Integer semestreFinanciero;
    private String materia;
    private Docente objDocente;
    private String grupoClase;
    private Integer creditos;
    private String tipo;
    private String horario;
    private String salon;
    private String estadoMatricula;
    private String observacion;
    private MatriculaAcademica objMatriculaAcademica;
}


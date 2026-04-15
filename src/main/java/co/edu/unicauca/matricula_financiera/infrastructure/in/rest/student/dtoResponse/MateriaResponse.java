package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MateriaResponse {
    private String codigoOid;
    private String materia;
    private Integer creditos;
    private String tipo;
    private String grupoClase;
    private String horario;
    private String salon;
    private String estadoMatricula;
    private String observacion;
    private DocenteResponse docente;
}

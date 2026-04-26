package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BecaDescuentoInfoResponse {
    private String tipo;
    private Float porcentaje;
    private String resolucion;
    private String estado;
    private String avaladoConcejo;
}

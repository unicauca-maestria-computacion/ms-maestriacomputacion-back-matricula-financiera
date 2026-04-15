package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BecaResponse {
    private String dedicacion;
    private String tipo;
    private String entidadAsociada;
    private String titulo;
    private Float porcentaje;
}

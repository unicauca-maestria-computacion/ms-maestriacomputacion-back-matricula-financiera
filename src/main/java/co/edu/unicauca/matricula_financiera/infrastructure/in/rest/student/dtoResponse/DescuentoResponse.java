package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoResponse {
    private String tipoDescuento;
    private Float porcentaje;
}

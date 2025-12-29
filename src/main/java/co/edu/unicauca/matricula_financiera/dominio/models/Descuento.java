package co.edu.unicauca.matricula_financiera.dominio.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Descuento {
    private String tipoDescuento;
    private Float porcentaje;
    private Estudiante estudiante;
}


package co.edu.unicauca.matricula_financiera.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Beca {
    private String dedicacion;
    private String tipo;
    private String entidadAsociada;
    private String titulo;
    private Float porcentaje;
    private Estudiante estudiante;
}

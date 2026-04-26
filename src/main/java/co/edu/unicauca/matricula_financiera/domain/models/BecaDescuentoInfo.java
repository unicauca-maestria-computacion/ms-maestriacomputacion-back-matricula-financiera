package co.edu.unicauca.matricula_financiera.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BecaDescuentoInfo {

    private String tipo;        // de solicitud_beca_descuento.tipo
    private Float  porcentaje;  // de solicitudes_en_concejo.porcentaje
    private String resolucion;  // de solicitudes_en_concejo.resolucion
    private String estado;      // de descuentos.estado (sin transformación)
    private String avaladoConcejo; // de solicitudes_en_concejo.avalado_concejo
}

package co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsignaturaExternaDto {
    private Long id;
    private String nombre;
    private String codigo;
    private String tipo;
    private Integer creditos;
}

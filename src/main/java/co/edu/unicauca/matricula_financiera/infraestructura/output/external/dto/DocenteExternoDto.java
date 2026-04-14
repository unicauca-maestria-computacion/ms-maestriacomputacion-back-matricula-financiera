package co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocenteExternoDto {
    private Long id;
    private PersonaExternaDto persona;
    private String codigo;
    private String facultad;
    private String departamento;
}

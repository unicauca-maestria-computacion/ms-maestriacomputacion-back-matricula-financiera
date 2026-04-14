package co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodoAcademicoExternoDto {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaFinMatricula;
    private Integer tagPeriodo;
    private String descripcion;
    private String estado;
}

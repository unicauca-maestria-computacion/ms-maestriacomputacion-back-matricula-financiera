package co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatriculaAcademicaExternaDto {
    private Long id;
    private CursoExternoDto curso;
    private PeriodoAcademicoExternoDto periodo;
    private String estado;
    private String observacion;
}

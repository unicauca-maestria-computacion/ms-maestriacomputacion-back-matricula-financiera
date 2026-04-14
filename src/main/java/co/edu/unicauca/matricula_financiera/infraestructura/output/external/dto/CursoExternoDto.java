package co.edu.unicauca.matricula_financiera.infraestructura.output.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CursoExternoDto {
    private Long id;
    private String grupo;
    private PeriodoAcademicoExternoDto periodo;
    private AsignaturaExternaDto asignatura;
    private List<DocenteExternoDto> docentes;
    private String horario;
    private String salon;
    private String observacion;
}

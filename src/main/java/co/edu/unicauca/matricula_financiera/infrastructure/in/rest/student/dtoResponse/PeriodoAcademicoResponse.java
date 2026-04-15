package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse;

import co.edu.unicauca.matricula_financiera.domain.enums.PeriodoEstado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoAcademicoResponse {
    private Long id;
    private Integer tagPeriodo;
    private Integer anio;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaFinMatricula;
    private String descripcion;
    private PeriodoEstado estado;
}

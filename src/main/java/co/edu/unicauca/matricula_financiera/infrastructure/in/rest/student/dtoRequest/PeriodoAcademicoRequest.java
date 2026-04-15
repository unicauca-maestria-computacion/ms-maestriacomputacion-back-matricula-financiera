package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoRequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoAcademicoRequest {

    @NotNull(message = "{validation.periodo.tagPeriodo.notNull}")
    private Integer tagPeriodo;

    @NotNull(message = "{validation.periodo.anio.notNull}")
    private Integer anio;
}

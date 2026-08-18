package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PeriodoAcademicoRequest",
        description = "Identifica el periodo academico sobre el que se realiza la consulta")
public class PeriodoAcademicoRequest {

    @NotNull(message = "{validation.periodo.tagPeriodo.notNull}")
    @Schema(description = "Semestre del periodo academico: 1 para el primero, 2 para el segundo",
            example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer tagPeriodo;

    @NotNull(message = "{validation.periodo.anio.notNull}")
    @Schema(description = "Anio del periodo academico",
            example = "2024", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer anio;
}

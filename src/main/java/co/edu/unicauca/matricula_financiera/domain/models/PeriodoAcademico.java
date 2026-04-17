package co.edu.unicauca.matricula_financiera.domain.models;

import co.edu.unicauca.matricula_financiera.domain.enums.PeriodoEstado;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PeriodoAcademico {
    private Long id;
    private Integer tagPeriodo;
    private Integer año;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaFinMatricula;
    private String descripcion;
    private PeriodoEstado estado;
    private List<MatriculaFinanciera> matriculasFinancieras;

    // getPeriodo() y getAño() como alias para compatibilidad
    public Integer getPeriodo() {
        return tagPeriodo;
    }

    public Integer getAño() {
        if (año != null) return año;
        return fechaInicio != null ? fechaInicio.getYear() : null;
    }
}

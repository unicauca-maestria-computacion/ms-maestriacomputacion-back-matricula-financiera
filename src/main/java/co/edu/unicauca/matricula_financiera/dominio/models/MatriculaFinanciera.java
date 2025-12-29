package co.edu.unicauca.matricula_financiera.dominio.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaFinanciera {
    private Date fechaMatricula;
    private Float valorMatricula;
    private Boolean pagada;
    private PeriodoAcademico objPeriodoAcademico;
}


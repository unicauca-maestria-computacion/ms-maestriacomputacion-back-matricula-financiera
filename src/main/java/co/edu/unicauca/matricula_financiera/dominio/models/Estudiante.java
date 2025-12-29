package co.edu.unicauca.matricula_financiera.dominio.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {
    private Integer codigo;
    private String cohorte;
    private String periodoIngreso;
    private Integer semestreFinanciero;
    private List<MatriculaFinanciera> matriculasFinancieras;
    private List<Descuento> descuentos;
    private List<Beca> becas;
    private List<MatriculaAcademica> matriculasAcademicas;
}


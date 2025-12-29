package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteDTORespuesta {
    private Integer codigo;
    private String cohorte;
    private String periodoIngreso;
    private Integer semestreFinanciero;
    private List<MatriculaFinancieraDTORespuesta> matriculasFinancieras;
    private List<DescuentoDTORespuesta> descuentos;
    private List<BecaDTORespuesta> becas;
    private List<MatriculaAcademicaDTORespuesta> matriculasAcademicas;
}


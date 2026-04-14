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
    private String codigo;
    private String nombre;
    private String apellido;
    private Long identificacion;
    private Integer cohorte;
    private String periodoIngreso;
    private Integer semestreFinanciero;
    private Integer semestreAcademico;
    private Integer valorEnSMLV;
    private List<DescuentoDTORespuesta> descuentos;
    private List<BecaDTORespuesta> becas;
    private List<MateriaDTORespuesta> materias;
}

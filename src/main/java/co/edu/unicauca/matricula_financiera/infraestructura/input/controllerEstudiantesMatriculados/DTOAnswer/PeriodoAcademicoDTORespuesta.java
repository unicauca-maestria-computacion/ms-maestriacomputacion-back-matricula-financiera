package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOAnswer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoAcademicoDTORespuesta {
    private Long id;
    private Integer tagPeriodo;
    private Integer periodo;   // derivado: igual a tagPeriodo
    private Integer año;       // derivado: año de fechaInicio
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaFinMatricula;
    private String descripcion;
    private String estado;
}

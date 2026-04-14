package co.edu.unicauca.matricula_financiera.infraestructura.input.controllerEstudiantesMatriculados.DTOPeticion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoAcademicoDTOPeticion {
    private Integer tagPeriodo;
    private Integer periodo;   // aceptado por compatibilidad, se usa como tagPeriodo si tagPeriodo es null
    private Integer año;       // aceptado por compatibilidad, ignorado (se usa fechaInicio)
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaFinMatricula;
    private String descripcion;
    private String estado;
}

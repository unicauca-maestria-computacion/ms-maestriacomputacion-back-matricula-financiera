package co.edu.unicauca.matricula_financiera.infrastructure.in.rest.student.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private String codigo;
    private String nombre;
    private String apellido;
    private Long identificacion;
    private Integer cohorte;
    private String periodoIngreso;
    private Integer semestreFinanciero;
    private Integer semestreAcademico;
    private Integer valorEnSMLV;
    private Boolean esEgresadoUnicauca;
    private Boolean aplicaVotacion;
    private List<MateriaResponse> materias;
    private List<BecaDescuentoInfoResponse> becasDescuentos;
    private Boolean estaPago;
    private String grupoNombre;
}

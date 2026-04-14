package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
public class EstudianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, length = 255)
    private String codigo;

    @Column(name = "cohorte")
    private Integer cohorte;

    @Column(name = "periodo_ingreso")
    private String periodoIngreso;

    @Column(name = "semestre_financiero")
    private Integer semestreFinanciero;

    @Column(name = "semestre_academico")
    private Integer semestreAcademico;
}

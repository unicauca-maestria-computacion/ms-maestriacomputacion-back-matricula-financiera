package co.edu.unicauca.matricula_financiera.infrastructure.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "es_egresado_unicauca", nullable = false)
    private Boolean esEgresadoUnicauca = false;
}

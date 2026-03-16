package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Entity
@Table(name = "estudiante")
@PrimaryKeyJoinColumn(name = "persona_id", referencedColumnName = "id")
@Data
@EqualsAndHashCode(callSuper = true)
public class EstudianteEntity extends PersonaEntity {

    @Column(name = "codigo", unique = true, nullable = false, length = 20)
    private String codigo;

    private String cohorte;

    @Column(name = "periodo_ingreso")
    private String periodoIngreso;

    @Column(name = "semestre_financiero")
    private Integer semestreFinanciero;

    @OneToMany(mappedBy = "objEstudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatriculaFinancieraEntity> matriculasFinancieras;

    @OneToMany(mappedBy = "objEstudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DescuentoEntity> descuentos;

    @OneToMany(mappedBy = "objEstudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BecaEntity> becas;
}

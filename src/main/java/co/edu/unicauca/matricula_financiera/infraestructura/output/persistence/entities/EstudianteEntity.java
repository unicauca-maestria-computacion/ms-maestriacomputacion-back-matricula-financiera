package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    
    @Column(name = "codigo", unique = true, nullable = false)
    private Integer codigo;
    
    @Column(name = "cohorte")
    private String cohorte;
    
    @Column(name = "periodo_ingreso")
    private String periodoIngreso;
    
    @Column(name = "semestre_financiero")
    private Integer semestreFinanciero;
    
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatriculaFinancieraEntity> matriculasFinancieras;
    
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DescuentoEntity> descuentos;
    
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BecaEntity> becas;
    
    // Nota: MatriculaAcademicaEntity no está definida en el diagrama, se puede agregar después si es necesario
    // @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<MatriculaAcademicaEntity> matriculasAcademicas;
}


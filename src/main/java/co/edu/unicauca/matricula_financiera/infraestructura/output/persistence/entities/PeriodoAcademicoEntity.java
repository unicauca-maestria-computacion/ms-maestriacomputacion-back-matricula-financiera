package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "periodos_academicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoAcademicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "periodo")
    private Integer periodo;
    
    @Column(name = "año")
    private Integer año;
    
    @OneToMany(mappedBy = "objPeriodoAcademico", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatriculaFinancieraEntity> matriculasFinancieras;
    
    // Nota: Estas clases no están definidas en el diagrama, se pueden agregar después si es necesario
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "configuracion_reporte_financiero_id")
    // private ConfiguracionReportesFinancieroEntity objConfiguracionReporteFinanciero;
    
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "configuracion_reporte_grupo_id")
    // private ConfiguracionReportesGruposEntity objConfiguracionReporteGrupo;
    
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "proyeccion_estudiante_id")
    // private ProyeccionEstudianteEntity objProyeccionEstudiante;
}


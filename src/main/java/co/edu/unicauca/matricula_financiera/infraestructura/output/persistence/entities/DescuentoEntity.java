package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "descuentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fechainiciodes")
    @Temporal(TemporalType.DATE)
    private Date fechainiciodes;

    @Column(name = "fechafindes")
    @Temporal(TemporalType.DATE)
    private Date fechafindes;

    @Column(name = "tipodes")
    private String tipodes;

    @Column(name = "porcentajedes")
    private Integer porcentajedes;

    @Column(name = "numactades")
    private String numactades;

    @Column(name = "fechaactades")
    @Temporal(TemporalType.DATE)
    private Date fechaactades;

    @Column(name = "numresoldes")
    private String numresoldes;

    @Column(name = "resoluciondes")
    private String resoluciondes;

    @Column(name = "poliza")
    private String poliza;

    @Column(name = "estado")
    private Boolean estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private EstudianteEntity objEstudiante;
}

package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "becas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BecaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "dedicador")
    private String dedicador;
    
    @Column(name = "entidad_asociada")
    private String entidadAsociada;
    
    @Column(name = "tipo")
    private String tipo;
    
    @Column(name = "titulo")
    private String titulo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id")
    private EstudianteEntity estudiante;
}


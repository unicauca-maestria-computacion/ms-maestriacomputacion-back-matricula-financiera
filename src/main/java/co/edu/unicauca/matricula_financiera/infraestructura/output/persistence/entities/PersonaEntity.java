package co.edu.unicauca.matricula_financiera.infraestructura.output.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public class PersonaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "identificacion")
    private Integer identificacion;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "nombre")
    private String nombre;
}

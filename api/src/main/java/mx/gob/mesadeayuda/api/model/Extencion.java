package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "EXTENCIONES")
@Getter
@Setter
public class Extencion {

    @Id
    @Column(name = "ID")
    private Long id; // extensión telefónica

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "DEPARTAMENTO")
    private String departamento;

    @Column(name = "CORREO")
    private String correo;
}

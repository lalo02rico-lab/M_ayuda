package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CORREOS")
@Getter
@Setter
public class Correo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USUARIO")
    private String usuario;

    @Column(name = "CORREO")
    private String correo;

    @Column(name = "TIPO_LICENCIA")
    private String tipoLicencia;

    @Column(name = "AREA")
    private String area;
}

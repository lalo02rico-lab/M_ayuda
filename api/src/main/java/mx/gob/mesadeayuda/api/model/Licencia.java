package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "LICENCIAS")
@Getter
@Setter
public class Licencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USUARIO")
    private String usuario;

    @Column(name = "AREA")
    private String area;

    @Column(name = "TOTAL_LICENCIAS")
    private Integer totalLicencias;
}

package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cat_tipo_solicitud")
@Data

public class CatTipoSolicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo")
    private Long idTipo;

    @Column(nullable = false, unique = true)
    private String clave;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;
}

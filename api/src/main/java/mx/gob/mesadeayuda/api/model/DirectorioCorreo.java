package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "directorio_correos")
public class DirectorioCorreo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreCompleto;
    private String correo;
    private String extension;
    private String puesto;
    private String area;
}

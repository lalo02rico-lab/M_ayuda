package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "PRE_SOLICITUD", schema = "MESADEAYUDA")
public class PreSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRE")
    private Long idPre;

    @Column(name = "NOMBRE", nullable = false, length = 120)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "ID_DEPARTAMENTO", nullable = false)
    private CatDepartamento departamento;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private Date fechaRegistro = new Date();
}

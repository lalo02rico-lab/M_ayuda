package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "tickets")
@Data

public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ticket")
    private Long idTicket;

    private String folio;

    @ManyToOne
    @JoinColumn(name = "id_pre", nullable = false)
    private PreSolicitud preSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    private CatTipoSolicitud tipoSolicitud;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_hora", nullable = false)
    private Date fechaHora;

    @Column(name = "id_estado", nullable = false)
    private Long idEstado = 1L; // Por defecto "NUEVO"

}

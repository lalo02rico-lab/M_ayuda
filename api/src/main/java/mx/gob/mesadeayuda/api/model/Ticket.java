package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "TICKETS")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TICKET")
    private Long idTicket;

    // 🔗 Relación con la tabla de pre-solicitud
    @ManyToOne
    @JoinColumn(name = "ID_PRE")
    private PreSolicitud preSolicitud;

    // 🔗 Relación con tipo de solicitud
    @ManyToOne
    @JoinColumn(name = "ID_TIPO")
    private CatTipoSolicitud tipoSolicitud;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "FECHA_HORA")
    private Timestamp fechaHora;

    @Column(name = "ID_ESTADO")
    private Long idEstado;

    // 🔗 Relación: técnico asignado
    @ManyToOne
    @JoinColumn(name = "ID_TECNICO", referencedColumnName = "ID_USUARIO")
    private Usuario tecnico;

    //NUEVO: Ruta temporal de la imagen adjunta s
    @Column(name = "RUTA_IMAGEN")
    private String rutaImagen;

    //nivel de atencion

    @Column(name = "NIVEL_ATENCION")
    private String nivelAtencion;



    // ======= GETTERS & SETTERS =======

    public Long getIdTicket() { return idTicket; }
    public void setIdTicket(Long idTicket) { this.idTicket = idTicket; }

    public PreSolicitud getPreSolicitud() { return preSolicitud; }
    public void setPreSolicitud(PreSolicitud preSolicitud) { this.preSolicitud = preSolicitud; }

    public CatTipoSolicitud getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(CatTipoSolicitud tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }

    public Long getIdEstado() { return idEstado; }
    public void setIdEstado(Long idEstado) { this.idEstado = idEstado; }

    public Usuario getTecnico() { return tecnico; }
    public void setTecnico(Usuario tecnico) { this.tecnico = tecnico; }

    // ⭐ Getter y Setter de rutaImagen ⭐
    public String getRutaImagen() { return rutaImagen; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }

//nivel de atencion
    public String getNivelAtencion() { return nivelAtencion; }
    public void setNivelAtencion(String nivelAtencion) { this.nivelAtencion = nivelAtencion; }

}

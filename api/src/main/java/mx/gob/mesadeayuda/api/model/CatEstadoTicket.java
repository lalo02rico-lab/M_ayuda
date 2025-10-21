package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CAT_ESTADO_TICKET")
public class CatEstadoTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESTADO")
    private Long idEstado;

    @Column(name = "CLAVE", length = 50, nullable = false)
    private String clave;

    @Column(name = "NOMBRE", length = 100, nullable = false)
    private String nombre;

    // Getters y Setters
    public Long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Long idEstado) {
        this.idEstado = idEstado;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

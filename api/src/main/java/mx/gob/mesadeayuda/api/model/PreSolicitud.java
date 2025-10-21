package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PRE_SOLICITUD")
public class PreSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PRE")
    private Long idPre;

    @Column(name = "NOMBRE", nullable = false, length = 120)
    private String nombre;

    // 🔹 Relación con la tabla de departamentos
    @ManyToOne
    @JoinColumn(name = "ID_DEPARTAMENTO", referencedColumnName = "ID_DEPARTAMENTO", nullable = false)
    private CatDepartamento departamento;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private Date fechaRegistro = new Date();

    // 🔸 Constructor vacío obligatorio
    public PreSolicitud() {}

    // 🔸 Getters y Setters
    public Long getIdPre() {
        return idPre;
    }

    public void setIdPre(Long idPre) {
        this.idPre = idPre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CatDepartamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(CatDepartamento departamento) {
        this.departamento = departamento;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}

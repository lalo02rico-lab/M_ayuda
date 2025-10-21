package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DIRECTORIO")
public class Directorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DIRECTORIO")
    private Long idDirectorio;

    @Column(name = "NOMBRE", length = 150)
    private String nombre;

    @Column(name = "CARGO", length = 100)
    private String cargo;

    @Column(name = "PROFESION", length = 100)
    private String profesion;

    @Column(name = "DEPENDENCIA", length = 150)
    private String dependencia;

    // 🔹 Getters y Setters
    public Long getIdDirectorio() {
        return idDirectorio;
    }

    public void setIdDirectorio(Long idDirectorio) {
        this.idDirectorio = idDirectorio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getProfesion() {
        return profesion;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }
}

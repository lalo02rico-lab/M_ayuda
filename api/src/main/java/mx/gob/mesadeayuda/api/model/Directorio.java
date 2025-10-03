package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "directorio") // tu tabla en Oracle
public class Directorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Oracle 12c+ soporta IDENTITY
    @Column(name = "ID_DIRECTORIO")
    private Long idDirectorio;

    @Column(name = "DEPENDENCIA")
    private String dependencia;

    @Column(name = "PROFESION")
    private String profesion;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "CARGO")
    private String cargo;

    // Getters & Setters
    public Long getIdDirectorio() { return idDirectorio; }
    public void setIdDirectorio(Long idDirectorio) { this.idDirectorio = idDirectorio; }

    public String getDependencia() { return dependencia; }
    public void setDependencia(String dependencia) { this.dependencia = dependencia; }

    public String getProfesion() { return profesion; }
    public void setProfesion(String profesion) { this.profesion = profesion; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}


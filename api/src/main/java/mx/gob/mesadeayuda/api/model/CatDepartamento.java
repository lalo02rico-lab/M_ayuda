package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CAT_DEPARTAMENTO")
public class CatDepartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DEPARTAMENTO")
    private Long idDepartamento;

    @Column(name = "NOMBRE_DEPARTAMENTO", nullable = false, length = 120)
    private String nombreDepartamento;

    // 🔸 Constructor vacío
    public CatDepartamento() {}

    // 🔸 Getters y Setters
    public Long getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }
}

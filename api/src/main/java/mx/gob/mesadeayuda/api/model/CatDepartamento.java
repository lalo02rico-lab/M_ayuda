package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "CAT_DEPARTAMENTO", schema = "MESADEAYUDA")
public class CatDepartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DEPARTAMENTO")
    private Long idDepartamento;

    @Column(name = "NOMBRE_DEPARTAMENTO", nullable = false, unique = true, length = 200)
    private String nombreDepartamento;
}

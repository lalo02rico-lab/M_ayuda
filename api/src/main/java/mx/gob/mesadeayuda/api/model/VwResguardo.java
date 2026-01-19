package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "VW_RESGUARDOS")
public class VwResguardo {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "TIPO_EQUIPO")
    private String tipoEquipo;

    @Column(name = "NO")
    private Integer no;

    @Column(name = "USUARIO")
    private String usuario;

    @Column(name = "USUARIO_FINAL")
    private String usuarioFinal;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "AREA")
    private String area;

    @Column(name = "PERFIL_EQUIPO")
    private String perfilEquipo;

    @Column(name = "MODELO")
    private String modelo;

    @Column(name = "SERIE_CPU")
    private String serieCpu;

    @Column(name = "MODELO_MONITOR")
    private String modeloMonitor;

    @Column(name = "SERIE_MONITOR")
    private String serieMonitor;

    @Column(name = "SERIE_UPS")
    private String serieUps;

    @Column(name = "SERIE_TECLADO")
    private String serieTeclado;

    @Column(name = "SERIE_MOUSE")
    private String serieMouse;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "OBSERVACIONES")
    private String observaciones;
}

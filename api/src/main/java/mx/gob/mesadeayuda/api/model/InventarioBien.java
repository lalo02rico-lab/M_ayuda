package mx.gob.mesadeayuda.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "INVENTARIO_BIENES_INSTITUCIONALES")
@Getter
@Setter
public class InventarioBien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INVENTARIO")
    private Long idInventario;

    @Column(name = "INVENTARIO")
    private String inventario;

    @Column(name = "NIC")
    private String nic;

    @Column(name = "DESCRIPCION_BIEN")
    private String descripcionBien;

    @Column(name = "VALOR")
    private BigDecimal valor;

    @Column(name = "MODELO")
    private String modelo;

    @Column(name = "MARCA")
    private String marca;

    @Column(name = "SERIE")
    private String serie;

    @Column(name = "CLAVE_DEPENDENCIA")
    private String claveDependencia;

    @Column(name = "NOMBRE_DEPENDENCIA")
    private String nombreDependencia;

    @Column(name = "RESGUARDATARIO")
    private String resguardatario;

    @Column(name = "NOMBRE_INMUEBLE")
    private String nombreInmueble;

    @Column(name = "PROVEEDOR")
    private String proveedor;

    @Column(name = "ESTADO_USO")
    private String estadoUso;

    @Column(name = "FECHA_ADQUISICION")
    private LocalDate fechaAdquisicion;

    @Column(name = "FECHA_ASIGNACION")
    private LocalDate fechaAsignacion;

    @Column(name = "FECHA_FIRMA_RESG")
    private LocalDate fechaFirmaResg;

    @Column(name = "SAL_MIN_UMA")
    private String salMinUma;

    @Column(name = "TIPO_PROPIEDAD")
    private String tipoPropiedad;

    @Column(name = "FORMA_ADQUISICION")
    private String formaAdquisicion;

    @Column(name = "TIPO_DOCUMENTO")
    private String tipoDocumento;

    @Column(name = "CARACTERISTICAS")
    private String caracteristicas;

    @Column(name = "MATERIAL")
    private String material;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "TIPO_ASIGNACION")
    private String tipoAsignacion;

    @Column(name = "PLACAS")
    private String placas;

    @Column(name = "ACTIVO_GENERICO")
    private String activoGenerico;

    @Column(name = "GRUPO_ACTIVO")
    private String grupoActivo;

    @Column(name = "ACTIVO_ESPECIFICO")
    private String activoEspecifico;
}
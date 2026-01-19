package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.InventarioBien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventarioBienRepository extends JpaRepository<InventarioBien, Long> {

    /* =================================================
       FILTRO GENERAL (USADO POR PDF)
    ================================================= */
    @Query("""
        SELECT i FROM InventarioBien i
        WHERE
            :q IS NULL OR
            LOWER(i.inventario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.nic) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.descripcionBien) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.marca) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.modelo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.serie) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.nombreDependencia) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.resguardatario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.estadoUso) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.grupoActivo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.activoEspecifico) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<InventarioBien> filtrar(@Param("q") String q);

    /* =================================================
       FILTRO CON PAGINACIÓN (USADO POR LA VISTA)
    ================================================= */
    @Query("""
        SELECT i FROM InventarioBien i
        WHERE
            :q IS NULL OR
            LOWER(i.inventario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.nic) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.descripcionBien) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.marca) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.modelo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.serie) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.nombreDependencia) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.resguardatario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.estadoUso) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.grupoActivo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(i.activoEspecifico) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    Page<InventarioBien> filtrar(@Param("q") String q, Pageable pageable);

    /* =================================================
       MÉTODOS EXISTENTES (EDITAR / ELIMINAR)
    ================================================= */

    // Para EDITAR (modal)
    InventarioBien findByInventario(String inventario);

    // Para ELIMINAR
    void deleteByInventario(String inventario);
}

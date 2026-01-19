package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.VwResguardo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VwResguardoRepository extends JpaRepository<VwResguardo, Long> {

    /* =================================================
       FILTRO NORMAL (USADO POR PDF)
    ================================================= */
    @Query("""
        SELECT r FROM VwResguardo r
        WHERE
        (:tipo IS NULL OR r.tipoEquipo = :tipo)
        AND (
            :q IS NULL OR
            LOWER(r.usuario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.usuarioFinal) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.direccion) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.area) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.perfilEquipo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.modelo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.serieCpu) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.status) LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    List<VwResguardo> filtrar(
            @Param("tipo") String tipo,
            @Param("q") String q
    );

    /* =================================================
       FILTRO CON PAGINACIÓN (USADO POR LA VISTA)
    ================================================= */
    @Query("""
        SELECT r FROM VwResguardo r
        WHERE
        (:tipo IS NULL OR r.tipoEquipo = :tipo)
        AND (
            :q IS NULL OR
            LOWER(r.usuario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.usuarioFinal) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.direccion) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.area) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.perfilEquipo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.modelo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.serieCpu) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(r.status) LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    Page<VwResguardo> filtrar(
            @Param("tipo") String tipo,
            @Param("q") String q,
            Pageable pageable
    );
}

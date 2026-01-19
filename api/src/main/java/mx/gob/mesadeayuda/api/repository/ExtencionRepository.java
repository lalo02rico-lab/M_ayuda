package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.Extencion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExtencionRepository extends JpaRepository<Extencion, Long> {

    /* =================================================
       FILTRO NORMAL (USADO POR PDF)
    ================================================= */
    @Query("""
        SELECT e FROM Extencion e
        WHERE
            :q IS NULL OR
            STR(e.id) LIKE CONCAT('%', :q, '%')
            OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(e.departamento) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(e.correo) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<Extencion> filtrar(@Param("q") String q);

    /* =================================================
       FILTRO CON PAGINACIÓN (USADO POR LA VISTA)
    ================================================= */
    @Query("""
        SELECT e FROM Extencion e
        WHERE
            :q IS NULL OR
            STR(e.id) LIKE CONCAT('%', :q, '%')
            OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(e.departamento) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(e.correo) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    Page<Extencion> filtrar(@Param("q") String q, Pageable pageable);
}

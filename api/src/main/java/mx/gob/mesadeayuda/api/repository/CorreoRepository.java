package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.Correo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CorreoRepository extends JpaRepository<Correo, Long> {

    /* =================================================
       FILTRO NORMAL (USADO POR PDF)
    ================================================= */
    @Query("""
        SELECT c FROM Correo c
        WHERE
            :q IS NULL OR
            LOWER(c.usuario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.correo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.tipoLicencia) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.area) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<Correo> filtrar(@Param("q") String q);

    /* =================================================
       FILTRO CON PAGINACIÓN (USADO POR LA VISTA)
    ================================================= */
    @Query("""
        SELECT c FROM Correo c
        WHERE
            :q IS NULL OR
            LOWER(c.usuario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.correo) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.tipoLicencia) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(c.area) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    Page<Correo> filtrar(@Param("q") String q, Pageable pageable);
}

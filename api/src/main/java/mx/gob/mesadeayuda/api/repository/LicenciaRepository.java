package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.Licencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LicenciaRepository extends JpaRepository<Licencia, Long> {

    /* =================================================
       FILTRO NORMAL (USADO POR PDF)
    ================================================= */
    @Query("""
        SELECT l FROM Licencia l
        WHERE
            :q IS NULL OR
            LOWER(l.usuario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(l.area) LIKE LOWER(CONCAT('%', :q, '%'))
            OR STR(l.totalLicencias) LIKE CONCAT('%', :q, '%')
    """)
    List<Licencia> filtrar(@Param("q") String q);

    /* =================================================
       FILTRO CON PAGINACIÓN (USADO POR LA VISTA)
    ================================================= */
    @Query("""
        SELECT l FROM Licencia l
        WHERE
            :q IS NULL OR
            LOWER(l.usuario) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(l.area) LIKE LOWER(CONCAT('%', :q, '%'))
            OR STR(l.totalLicencias) LIKE CONCAT('%', :q, '%')
    """)
    Page<Licencia> filtrar(@Param("q") String q, Pageable pageable);
}

package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.Directorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorioRepository extends JpaRepository<Directorio, Long> {

    @Query("SELECT d FROM Directorio d " +
            "WHERE UPPER(d.nombre) LIKE :filtro " +
            "OR UPPER(d.cargo) LIKE :filtro " +
            "OR UPPER(d.profesion) LIKE :filtro " +
            "OR UPPER(d.dependencia) LIKE :filtro")
    Page<Directorio> findByFiltro(String filtro, Pageable pageable);
}

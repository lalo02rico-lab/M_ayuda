package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.Directorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface DirectorioRepository extends JpaRepository<Directorio, Long> {
    Page<Directorio> findByDependenciaContainingIgnoreCaseAndProfesionContainingIgnoreCaseAndNombreContainingIgnoreCaseAndCargoContainingIgnoreCase(
            String dependencia, String profesion, String nombre, String cargo, Pageable pageable);

}

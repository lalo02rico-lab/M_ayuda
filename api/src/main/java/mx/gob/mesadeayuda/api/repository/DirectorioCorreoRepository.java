package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.DirectorioCorreo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DirectorioCorreoRepository extends JpaRepository<DirectorioCorreo, Long> {

    @Query("SELECT DISTINCT d.area FROM DirectorioCorreo d ORDER BY d.area")
    List<String> obtenerAreas();

    List<DirectorioCorreo> findByAreaOrderByNombreCompletoAsc(String area);
    List<DirectorioCorreo> findAllByOrderByAreaAscNombreCompletoAsc();


}

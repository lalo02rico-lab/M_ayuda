package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.CatTipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatTipoSolicitudRepository extends JpaRepository<CatTipoSolicitud, Long> {}


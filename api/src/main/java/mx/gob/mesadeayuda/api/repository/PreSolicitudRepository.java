package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.PreSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreSolicitudRepository extends JpaRepository<PreSolicitud, Long> {}


package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.CatEstadoTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatEstadoTicketRepository extends JpaRepository<CatEstadoTicket, Long> {
    CatEstadoTicket findByClave(String clave);
}

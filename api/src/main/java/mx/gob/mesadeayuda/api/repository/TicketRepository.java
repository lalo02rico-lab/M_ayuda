package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}

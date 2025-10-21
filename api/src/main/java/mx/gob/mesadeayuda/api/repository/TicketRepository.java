package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // 🔹 Tickets nuevos (por estado)

    // Buscar tickets por estado
    List<Ticket> findByIdEstado(Long idEstado);

    // 🔥 ORDEN CORRECTO (ASC) = MÁS VIEJOS ARRIBA
    List<Ticket> findByIdEstadoOrderByFechaHoraAsc(Long idEstado);

    // 🔥 Tickets asignados a un técnico ordenados por fecha
    List<Ticket> findByTecnicoIdUsuarioOrderByFechaHoraAsc(Long idTecnico);

    // ⭐⭐⭐ NUEVO MÉTODO: traer solo tickets NO finalizados
    List<Ticket> findByTecnicoIdUsuarioAndIdEstadoNotOrderByFechaHoraAsc(Long idTecnico, Long idEstado);

}

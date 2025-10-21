package mx.gob.mesadeayuda.api.controller;

import mx.gob.mesadeayuda.api.model.Ticket;
import mx.gob.mesadeayuda.api.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/tecnico")
public class TecnicoController {

    @Autowired
    private TicketRepository ticketRepository;

    // MENU PRINCIPAL
    @GetMapping("/menu")
    public String menuTecnico(@RequestParam("idTecnico") Long idTecnico, Model model) {
        model.addAttribute("idTecnico", idTecnico);
        return "menu_tecnico";
    }

    // LISTA DE TICKETS ACTIVOS
    @GetMapping("/tickets")
    public String ticketsTecnico(@RequestParam("idTecnico") Long idTecnico, Model model) {
        List<Ticket> tickets =
                ticketRepository.findByTecnicoIdUsuarioAndIdEstadoNotOrderByFechaHoraAsc(idTecnico, 5L);

        model.addAttribute("tickets", tickets);
        model.addAttribute("idTecnico", idTecnico);
        return "tecnico_tickets";
    }

    // DETALLE DEL TICKET
    @GetMapping("/detalle/{id}")
    public String detalleTicket(@PathVariable("id") Long idTicket,
                                @RequestParam("idTecnico") Long idTecnico,
                                Model model) {

        Ticket ticket = ticketRepository.findById(idTicket).orElse(null);

        model.addAttribute("ticket", ticket);
        model.addAttribute("idTecnico", idTecnico);

        return "detalle_ticket";
    }

    // ACCIONES DEL TÉCNICO
    @PostMapping("/asumir")
    public String asumirTicket(@RequestParam("idTicket") Long idTicket,
                               @RequestParam("idTecnico") Long idTecnico) {

        Ticket ticket = ticketRepository.findById(idTicket).orElse(null);
        ticket.setIdEstado(3L);  // EN PROCESO (ASUMIDO)
        ticketRepository.save(ticket);

        return "redirect:/tecnico/estado?idTecnico=" + idTecnico;
    }

    @PostMapping("/pendiente")
    public String pendienteTicket(@RequestParam("idTicket") Long idTicket,
                                  @RequestParam("idTecnico") Long idTecnico) {

        Ticket ticket = ticketRepository.findById(idTicket).orElse(null);
        ticket.setIdEstado(4L); // PENDIENTE
        ticketRepository.save(ticket);

        return "redirect:/tecnico/estado?idTecnico=" + idTecnico;
    }

    // ⭐⭐⭐ FINALIZAR (con eliminación de imagen temporal) ⭐⭐⭐
    @PostMapping("/finalizar")
    public String finalizarTicket(@RequestParam("idTicket") Long idTicket,
                                  @RequestParam("idTecnico") Long idTecnico) {

        Ticket ticket = ticketRepository.findById(idTicket).orElse(null);

        if (ticket != null) {

            // 🔥 ELIMINAR ARCHIVO DE IMAGEN TEMPORAL
            if (ticket.getRutaImagen() != null) {

                File archivo = new File(ticket.getRutaImagen());

                // Borrar archivo
                if (archivo.exists()) archivo.delete();

                // Borrar carpeta si queda vacía
                File carpeta = archivo.getParentFile();
                if (carpeta.exists() && carpeta.isDirectory()) carpeta.delete();

                ticket.setRutaImagen(null);  // Limpiar referencia
            }

            ticket.setIdEstado(5L); // FINALIZADO
            ticketRepository.save(ticket);
        }

        return "redirect:/tecnico/estado?idTecnico=" + idTecnico;
    }

    // VISTA DE ESTADO
    @GetMapping("/estado")
    public String estadoTickets(@RequestParam("idTecnico") Long idTecnico, Model model) {

        List<Ticket> tickets =
                ticketRepository.findByTecnicoIdUsuarioOrderByFechaHoraAsc(idTecnico);

        model.addAttribute("tickets", tickets);
        model.addAttribute("idTecnico", idTecnico);

        return "tecnico_estado";
    }
}

package mx.gob.mesadeayuda.api.controller;

import jakarta.servlet.http.HttpSession;
import mx.gob.mesadeayuda.api.model.Ticket;
import mx.gob.mesadeayuda.api.model.Usuario;
import mx.gob.mesadeayuda.api.repository.TicketRepository;
import mx.gob.mesadeayuda.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ==========================
    //  MENU PRINCIPAL ADMIN
    // ==========================
    @GetMapping("/menu")
    public String menuAdmin(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) return "redirect:/login";

        List<Ticket> ticketsNuevos = ticketRepository.findByIdEstadoOrderByFechaHoraAsc(1L);


        model.addAttribute("ticketsNuevos", ticketsNuevos);
        model.addAttribute("nuevos", ticketsNuevos.size());
        model.addAttribute("nombre", usuario.getNombreUsuario());

        return "menu_admin";
    }

    // ==========================
    // LISTA DE TICKETS A ASIGNAR
    // ==========================
    @GetMapping("/tickets")
    public String verTicketsNuevos(Model model) {

        //List<Ticket> ticketsNuevos = ticketRepository.findByIdEstado(1L);

        List<Ticket> ticketsNuevos = ticketRepository.findByIdEstadoOrderByFechaHoraAsc(1L);


        // Todos los técnicos registrados
        List<Usuario> tecnicos = usuarioRepository.findByRolNombreIgnoreCase("TECNICO");

        model.addAttribute("tickets", ticketsNuevos);
        model.addAttribute("tecnicos", tecnicos);

        return "admin_tickets";
    }

    // ==========================
    // ASIGNAR TICKET A TÉCNICO
    // ==========================
    @PostMapping("/asignar")
    public String asignarTicket(
            @RequestParam("idTicket") Long idTicket,
            @RequestParam("idTecnico") Long idTecnico
    ) {

        Ticket ticket = ticketRepository.findById(idTicket).orElse(null);
        Usuario tecnico = usuarioRepository.findById(idTecnico).orElse(null);

        if (ticket != null && tecnico != null) {
            ticket.setTecnico(tecnico); // asigna el tecnico
            ticket.setIdEstado(2L);     // 2 = ASIGNADO
            ticketRepository.save(ticket);
        }

        return "redirect:/admin/tickets";
    }


}

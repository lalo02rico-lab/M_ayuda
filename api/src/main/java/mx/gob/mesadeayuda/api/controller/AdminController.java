package mx.gob.mesadeayuda.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mx.gob.mesadeayuda.api.model.Ticket;
import mx.gob.mesadeayuda.api.model.Usuario;
import mx.gob.mesadeayuda.api.repository.TicketRepository;
import mx.gob.mesadeayuda.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ==========================
    //  MENÚ PRINCIPAL ADMIN
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
    // DASHBOARD (PANTALLA COMPLETA)
    // ==========================
    @GetMapping("/dashboard")
    public String verDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            Model model) {

        DashboardData data = construirDashboardData(inicio, fin);

        model.addAttribute("data", data);
        model.addAttribute("inicio", inicio);
        model.addAttribute("fin", fin);

        return "dashboard_admin";
    }

    // ==========================
    // DATOS DEL DASHBOARD (AJAX)
    // ==========================
    @GetMapping("/dashboard/datos")
    @ResponseBody
    public DashboardData obtenerDatosDashboard(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        return construirDashboardData(inicio, fin);
    }

    // ==========================
    // EXPORTAR CSV
    // ==========================
    @GetMapping("/dashboard/export")
    public void exportarBitacoraExcel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
            HttpServletResponse response) throws IOException {

        final Long ESTADO_FINALIZADO = 5L; // 👈 AJUSTA A TU ID REAL

        List<Ticket> tickets = ticketRepository.findByIdEstado(ESTADO_FINALIZADO);

        // =========================
        // FILTRO POR FECHAS (FECHA FIN)
        // =========================
        if (inicio != null && fin != null) {
            Timestamp desde = Timestamp.valueOf(inicio.atStartOfDay());
            Timestamp hasta = Timestamp.valueOf(fin.atTime(23, 59, 59));

            tickets = tickets.stream()
                    .filter(t -> t.getFechaFin() != null
                            && !t.getFechaFin().before(desde)
                            && !t.getFechaFin().after(hasta))
                    .toList();
        }

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Bitacora_Soporte_Tecnico.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bitácora");

        // =========================
        // ESTILO ENCABEZADOS
        // =========================
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // =========================
        // ENCABEZADOS
        // =========================
        Row header = sheet.createRow(0);
        String[] columnas = {
                "Reporte / solución",
                "Unidad Administrativa",
                "Servidor Público Usuario",
                "Fecha de Inicio",
                "Fecha Fin"
        };

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // =========================
        // DATOS
        // =========================
        int rowNum = 1;

        for (Ticket t : tickets) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(
                    t.getDescripcion() != null ? t.getDescripcion() : "-"
            );

            row.createCell(1).setCellValue(
                    t.getPreSolicitud() != null &&
                            t.getPreSolicitud().getDepartamento() != null
                            ? t.getPreSolicitud().getDepartamento().getNombreDepartamento()
                            : "-"
            );

            row.createCell(2).setCellValue(
                    t.getPreSolicitud() != null
                            ? t.getPreSolicitud().getNombre()
                            : "-"
            );

            row.createCell(3).setCellValue(
                    t.getFechaHora() != null
                            ? t.getFechaHora().toString()
                            : "-"
            );

            row.createCell(4).setCellValue(
                    t.getFechaFin() != null
                            ? t.getFechaFin().toString()
                            : "-"
            );
        }

        // =========================
        // AUTO AJUSTE
        // =========================
        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }



    // ==========================
    // LISTA DE TICKETS NUEVOS
    // ==========================
    @GetMapping("/tickets")
    public String verTicketsNuevos(Model model) {

        List<Ticket> ticketsNuevos = ticketRepository.findByIdEstadoOrderByFechaHoraAsc(1L);
        List<Usuario> tecnicos = usuarioRepository.findByRolNombreIgnoreCase("TECNICO");

        model.addAttribute("tickets", ticketsNuevos);
        model.addAttribute("tecnicos", tecnicos);

        return "admin_tickets";
    }

    // ==========================
    // ASIGNAR TICKET NUEVO
    // ==========================
    @PostMapping("/asignar")
    public String asignarTicket(
            @RequestParam("idTicket") Long idTicket,
            @RequestParam("idTecnico") Long idTecnico
    ) {
        Ticket ticket = ticketRepository.findById(idTicket).orElse(null);
        Usuario tecnico = usuarioRepository.findById(idTecnico).orElse(null);

        if (ticket != null && tecnico != null) {
            ticket.setTecnico(tecnico);
            ticket.setIdEstado(2L); // ASIGNADO
            ticketRepository.save(ticket);
        }

        return "redirect:/admin/tickets";
    }

    // ==========================
    // LISTA DE TICKETS ASIGNADOS
    // ==========================
    @GetMapping("/tickets/asignados")
    public String verTicketsAsignados(Model model) {

        List<Ticket> ticketsAsignados = ticketRepository.findByIdEstadoOrderByFechaHoraAsc(2L);
        List<Usuario> tecnicos = usuarioRepository.findByRolNombreIgnoreCase("TECNICO");

        model.addAttribute("tickets", ticketsAsignados);
        model.addAttribute("tecnicos", tecnicos);

        return "admin_tickets_asignados";
    }

    // ==========================
    // REASIGNAR TICKET
    // ==========================
    @PostMapping("/reasignar")
    public String reasignarTicket(
            @RequestParam("idTicket") Long idTicket,
            @RequestParam("idTecnico") Long idTecnico
    ) {

        Ticket ticket = ticketRepository.findById(idTicket).orElse(null);
        Usuario tecnico = usuarioRepository.findById(idTecnico).orElse(null);

        if (ticket != null && tecnico != null) {
            ticket.setTecnico(tecnico);
            ticket.setIdEstado(2L); // sigue asignado
            ticketRepository.save(ticket);
        }

        return "redirect:/admin/tickets/asignados";
    }

    // ==========================
    // LÓGICA DEL DASHBOARD
    // ==========================
    private DashboardData construirDashboardData(LocalDate inicio, LocalDate fin) {

        List<Ticket> tickets = ticketRepository.findAll();

        if (inicio != null && fin != null) {
            Timestamp desde = Timestamp.valueOf(LocalDateTime.of(inicio, LocalTime.MIN));
            Timestamp hasta = Timestamp.valueOf(LocalDateTime.of(fin, LocalTime.MAX));

            tickets = tickets.stream()
                    .filter(t -> t.getFechaHora() != null
                            && !t.getFechaHora().before(desde)
                            && !t.getFechaHora().after(hasta))
                    .collect(Collectors.toList());
        }

        DashboardData data = new DashboardData();

        data.setTotal(tickets.size());
        data.setNuevos(contarPorEstado(tickets, 1L));
        data.setPendientes(contarPorEstado(tickets, 4L));
        data.setEnProceso(contarPorEstado(tickets, 3L));
        data.setFinalizados(contarPorEstado(tickets, 5L));

        // Categorías
        Map<String, Long> porCategoria = tickets.stream()
                .filter(t -> t.getTipoSolicitud() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getTipoSolicitud().getNombre(),
                        Collectors.counting()
                ));
        data.setPorCategoria(porCategoria);

        // Técnicos finalizados
        Map<String, Long> porTecnico = tickets.stream()
                .filter(t -> t.getIdEstado() != null && t.getIdEstado() == 5L)
                .filter(t -> t.getTecnico() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getTecnico().getNombreUsuario(),
                        Collectors.counting()
                ));
        data.setPorTecnicoFinalizados(porTecnico);

        // Últimos tickets
        List<DashboardData.TicketResumen> ultimos = tickets.stream()
                .sorted(Comparator.comparing(Ticket::getFechaHora,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(5)
                .map(DashboardData.TicketResumen::fromTicket)
                .collect(Collectors.toList());
        data.setUltimosTickets(ultimos);

        return data;
    }

    private long contarPorEstado(List<Ticket> tickets, Long estado) {
        return tickets.stream()
                .filter(t -> t.getIdEstado() != null && t.getIdEstado().equals(estado))
                .count();
    }

    // ==========================
    // DTO PARA EL DASHBOARD
    // ==========================
    public static class DashboardData {
        private long total;
        private long nuevos;
        private long pendientes;
        private long enProceso;
        private long finalizados;

        private Map<String, Long> porCategoria = new LinkedHashMap<>();
        private Map<String, Long> porTecnicoFinalizados = new LinkedHashMap<>();
        private List<TicketResumen> ultimosTickets = new ArrayList<>();

        // GETTERS & SETTERS...
        // (los dejo iguales, no necesitan cambios)

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }

        public long getNuevos() { return nuevos; }
        public void setNuevos(long nuevos) { this.nuevos = nuevos; }

        public long getPendientes() { return pendientes; }
        public void setPendientes(long pendientes) { this.pendientes = pendientes; }

        public long getEnProceso() { return enProceso; }
        public void setEnProceso(long enProceso) { this.enProceso = enProceso; }

        public long getFinalizados() { return finalizados; }
        public void setFinalizados(long finalizados) { this.finalizados = finalizados; }

        public Map<String, Long> getPorCategoria() { return porCategoria; }
        public void setPorCategoria(Map<String, Long> porCategoria) { this.porCategoria = porCategoria; }

        public Map<String, Long> getPorTecnicoFinalizados() { return porTecnicoFinalizados; }
        public void setPorTecnicoFinalizados(Map<String, Long> porTecnicoFinalizados) { this.porTecnicoFinalizados = porTecnicoFinalizados; }

        public List<TicketResumen> getUltimosTickets() { return ultimosTickets; }
        public void setUltimosTickets(List<TicketResumen> ultimosTickets) { this.ultimosTickets = ultimosTickets; }

        // =====================
        // TicketResumen
        // =====================
        public static class TicketResumen {
            private Long idTicket;
            private String solicitante;
            private String categoria;
            private String estado;
            private String tecnico;
            private String nivelAtencion;
            private String fecha;

            public static TicketResumen fromTicket(Ticket t) {
                TicketResumen r = new TicketResumen();
                r.idTicket = t.getIdTicket();
                r.solicitante = t.getPreSolicitud() != null ? t.getPreSolicitud().getNombre() : "-";
                r.categoria = t.getTipoSolicitud() != null ? t.getTipoSolicitud().getNombre() : "-";
                r.estado = estadoTexto(t.getIdEstado());
                r.tecnico = t.getTecnico() != null ? t.getTecnico().getNombreUsuario() : "-";
                r.nivelAtencion = t.getNivelAtencion() != null ? t.getNivelAtencion() : "-";
                r.fecha = t.getFechaHora() != null ? t.getFechaHora().toString() : "";
                return r;
            }

            private static String estadoTexto(Long idEstado) {
                if (idEstado == null) return "-";
                return switch (idEstado.intValue()) {
                    case 1 -> "Nuevo";
                    case 2 -> "Asignado";
                    case 3 -> "En proceso";
                    case 4 -> "Pendiente";
                    case 5 -> "Finalizado";
                    default -> "Desconocido";
                };
            }

            public Long getIdTicket() { return idTicket; }
            public String getSolicitante() { return solicitante; }
            public String getCategoria() { return categoria; }
            public String getEstado() { return estado; }
            public String getTecnico() { return tecnico; }
            public String getNivelAtencion() { return nivelAtencion; }
            public String getFecha() { return fecha; }
        }
    }
}

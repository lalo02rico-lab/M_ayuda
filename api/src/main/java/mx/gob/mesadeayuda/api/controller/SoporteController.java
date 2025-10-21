package mx.gob.mesadeayuda.api.controller;

import mx.gob.mesadeayuda.api.model.CatDepartamento;
import mx.gob.mesadeayuda.api.model.CatTipoSolicitud;
import mx.gob.mesadeayuda.api.model.PreSolicitud;
import mx.gob.mesadeayuda.api.model.Ticket;
import mx.gob.mesadeayuda.api.repository.CatDepartamentoRepository;
import mx.gob.mesadeayuda.api.repository.CatTipoSolicitudRepository;
import mx.gob.mesadeayuda.api.repository.PreSolicitudRepository;
import mx.gob.mesadeayuda.api.repository.TicketRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

// PDF
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Controller
@RequestMapping("/soporte")
public class SoporteController {

    @Autowired
    private PreSolicitudRepository preSolicitudRepository;

    @Autowired
    private CatDepartamentoRepository catDepartamentoRepository;

    @Autowired
    private CatTipoSolicitudRepository catTipoSolicitudRepository;

    @Autowired
    private TicketRepository ticketRepository;


    // ===============================
    // PÁGINA PRINCIPAL
    // ===============================
    @GetMapping("/solicitud")
    public String mostrarFormularioInicial(Model model) {
        List<CatDepartamento> departamentos = catDepartamentoRepository.findAll();
        model.addAttribute("departamentos", departamentos);
        return "solicitud_soporte";
    }


    // ===============================
    // GUARDA LA PRE-SOLICITUD
    // ===============================
    @PostMapping("/guardar_registro")
    public String guardarPreRegistro(@RequestParam String nombre,
                                     @RequestParam Long departamento,
                                     Model model) {

        PreSolicitud pre = new PreSolicitud();
        pre.setNombre(nombre);

        CatDepartamento dep = catDepartamentoRepository.findById(departamento).orElse(null);
        pre.setDepartamento(dep);
        pre.setFechaRegistro(new Date());

        preSolicitudRepository.save(pre);

        List<CatTipoSolicitud> categorias = catTipoSolicitudRepository.findAll();
        model.addAttribute("categorias", categorias);
        model.addAttribute("idPre", pre.getIdPre());

        return "categorias_soporte";
    }


    // ===============================
    // FORMULARIO SEGÚN CATEGORÍA
    // ===============================
    @PostMapping("/formulario")
    public String mostrarFormulario(@RequestParam Long idPre,
                                    @RequestParam Long idTipo,
                                    Model model) {

        PreSolicitud pre = preSolicitudRepository.findById(idPre).orElse(null);
        CatTipoSolicitud tipo = catTipoSolicitudRepository.findById(idTipo).orElse(null);

        model.addAttribute("pre", pre);
        model.addAttribute("tipo", tipo);

        return "formulario_soporte";
    }


    // ===============================
    // GUARDAR TICKET + IMAGEN TEMPORAL + NIVEL DE ATENCIÓN
    // ===============================
    @PostMapping("/guardar_ticket")
    public String guardarTicket(@RequestParam Long idPre,
                                @RequestParam Long idTipo,
                                @RequestParam String descripcion,
                                @RequestParam(value = "archivo", required = false) MultipartFile archivo,
                                Model model) throws IOException {

        PreSolicitud pre = preSolicitudRepository.findById(idPre).orElse(null);
        CatTipoSolicitud tipo = catTipoSolicitudRepository.findById(idTipo).orElse(null);

        Ticket ticket = new Ticket();
        ticket.setPreSolicitud(pre);
        ticket.setTipoSolicitud(tipo);
        ticket.setDescripcion(descripcion);
        ticket.setFechaHora(new java.sql.Timestamp(System.currentTimeMillis()));
        ticket.setIdEstado(1L); // NUEVO


        // ----------------------------------------------------------------
        // NIVEL DE ATENCIÓN AUTOMÁTICO SEGÚN PALABRAS CLAVE
        // ----------------------------------------------------------------
        String desc = descripcion.toLowerCase();

        String[] bajo = {
                "office","impresora","red","dns","sitios","internet","conexion","carpetas",
                "escaner","controlador","navegador","actualizacion","kepler","meta",
                "formateo","filezilla"
        };

        String[] medio = {
                "mantenimiento","difusion","respaldo","desarrollo tecnico",
                "correo","licencia","contrasena","equipo","telefono"
        };

        String[] alto = {
                "aclaracion","difusion informatica","desarrollo","implementacion"
        };

        String nivel = "BAJO"; // default

        for(String w : alto) {
            if(desc.contains(w)) { nivel = "ALTO"; break; }
        }
        if(!nivel.equals("ALTO")){
            for(String w : medio){
                if(desc.contains(w)){ nivel = "MEDIO"; break; }
            }
        }

        ticket.setNivelAtencion(nivel);
        // ----------------------------------------------------------------


        ticketRepository.save(ticket); // obtener ID


        // ===== GUARDAR ARCHIVO TEMPORAL =====
        if (archivo != null && !archivo.isEmpty()) {

            String folder = "uploads/tickets/" + ticket.getIdTicket();
            File dir = new File(folder);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            Path path = Paths.get(folder + "/" + archivo.getOriginalFilename());
            Files.copy(archivo.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            ticket.setRutaImagen(path.toString());
        }

        ticketRepository.save(ticket);

        model.addAttribute("ticket", ticket);
        model.addAttribute("pre", pre);
        model.addAttribute("tipo", tipo);

        return "ticket_generado";
    }


    // ===============================
    // SERVIR IMAGEN TEMPORAL
    // ===============================
    @GetMapping("/img_temp/{idTicket}")
    @ResponseBody
    public ResponseEntity<byte[]> mostrarImagenTemporal(@PathVariable Long idTicket) {

        Ticket ticket = ticketRepository.findById(idTicket).orElse(null);

        if (ticket == null || ticket.getRutaImagen() == null)
            return ResponseEntity.notFound().build();

        try {
            File archivo = new File(ticket.getRutaImagen());

            if (!archivo.exists())
                return ResponseEntity.notFound().build();

            byte[] imagen = Files.readAllBytes(archivo.toPath());

            return ResponseEntity
                    .ok()
                    .header("Content-Type", "image/*")
                    .body(imagen);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }



    // ===============================
    // PDF SIMPLE
    // ===============================
    @GetMapping("/pdf/{idTicket}")
    public void generarPDF(@PathVariable Long idTicket,
                           HttpServletResponse response) throws Exception {

        Ticket ticket = ticketRepository.findById(idTicket).orElse(null);
        if (ticket == null) return;

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=ticket_" + idTicket + ".pdf");

        Document pdf = new Document();
        PdfWriter.getInstance(pdf, response.getOutputStream());
        pdf.open();

        pdf.add(new Paragraph("TICKET DE SOPORTE"));
        pdf.add(new Paragraph(" "));
        pdf.add(new Paragraph("ID Ticket: " + ticket.getIdTicket()));
        pdf.add(new Paragraph("Solicitante: " + ticket.getPreSolicitud().getNombre()));
        pdf.add(new Paragraph("Departamento: " + ticket.getPreSolicitud().getDepartamento().getNombreDepartamento()));
        pdf.add(new Paragraph("Nivel de Atención: " + ticket.getNivelAtencion()));
        pdf.add(new Paragraph("Fecha: " + ticket.getFechaHora()));
        pdf.add(new Paragraph(" "));
        pdf.add(new Paragraph("Descripción del problema:"));
        pdf.add(new Paragraph(ticket.getDescripcion()));

        pdf.close();
    }

}

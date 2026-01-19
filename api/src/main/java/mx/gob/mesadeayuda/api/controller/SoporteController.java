package mx.gob.mesadeayuda.api.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
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


import com.itextpdf.text.PageSize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.itextpdf.text.Document;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;



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
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=ticket_" + idTicket + ".pdf"
        );

        Document pdf = new Document(PageSize.LETTER);
        PdfWriter writer = PdfWriter.getInstance(pdf, response.getOutputStream());
        pdf.open();

        // =============================
        //   MÁRGENES Y ESTILOS
        // =============================
        pdf.setMargins(50, 50, 60, 60);

        Font titulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font label = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normal = new Font(Font.FontFamily.HELVETICA, 12);

        // =============================
        // LOGOS SUPERIORES
        // =============================
        try {
            Image logoGob = Image.getInstance(
                    getClass().getResource("/static/img/logo_gobierno.png")
            );
            logoGob.scaleToFit(120, 120);
            logoGob.setAbsolutePosition(60, 750);
            pdf.add(logoGob);



        } catch (Exception e) {
            System.out.println("❌ Error cargando logos: " + e.getMessage());
        }

        // =============================
        // LÍNEA DORADA ARRIBA
        // =============================
        PdfContentByte canvas = writer.getDirectContent();
        canvas.setLineWidth(3f);
        canvas.setColorStroke(new BaseColor(180, 137, 84)); // dorado
        canvas.moveTo(50, 665);
        canvas.lineTo(560, 665);
        canvas.stroke();

        pdf.add(new Paragraph("\n\n"));

        // =============================
        // TÍTULO CENTRAL
        // =============================
        Paragraph tituloDoc = new Paragraph("COMPROBANTE DE TICKET DE SOPORTE", titulo);
        tituloDoc.setAlignment(Element.ALIGN_CENTER);
        pdf.add(tituloDoc);

        pdf.add(new Paragraph("\n\n"));

        // =============================
        // MARCA DE AGUA CENTRAL
        // =============================
        try {
            Image marca = Image.getInstance(
                    getClass().getResource("/static/img/colibri.png")
            );
            marca.scaleToFit(380, 380);
            marca.setAbsolutePosition(110, 180);

            PdfContentByte under = writer.getDirectContentUnder();
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.12f); // transparente
            under.setGState(gs);
            under.addImage(marca);

        } catch (Exception e) {
            System.out.println("❌ No se cargó marca de agua: " + e.getMessage());
        }

        // =============================
        // CONTENIDO DEL TICKET
        // =============================
        PdfPTable tabla = new PdfPTable(2);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(20);
        tabla.setWidths(new float[]{35, 65});

        tabla.addCell(celdaTitulo("Fecha y hora:", label));
        tabla.addCell(celdaNormal(ticket.getFechaHora().toString(), normal));

        tabla.addCell(celdaTitulo("Nombre:", label));
        tabla.addCell(celdaNormal(ticket.getPreSolicitud().getNombre(), normal));

        tabla.addCell(celdaTitulo("Departamento:", label));
        tabla.addCell(celdaNormal(ticket.getPreSolicitud().getDepartamento().getNombreDepartamento(), normal));

        tabla.addCell(celdaTitulo("Descripción del soporte:", label));
        tabla.addCell(celdaNormal(ticket.getDescripcion(), normal));

        pdf.add(tabla);

        pdf.add(new Paragraph("\n\n\n"));

        // =============================
        // FIRMA
        // =============================
        Paragraph firma = new Paragraph("______________________________\nFIRMA DEL SERVIDOR PÚBLICO", label);
        firma.setAlignment(Element.ALIGN_CENTER);
        pdf.add(firma);

        pdf.add(new Paragraph("\n\n"));

        // =============================
        // CINTILLA INFERIOR
        // =============================
        try {
            Image cinta = Image.getInstance(
                    getClass().getResource("/static/img/cintilla.jpg")
            );
            cinta.scaleToFit(450, 80);
            cinta.setAbsolutePosition(80, 40);
            pdf.add(cinta);

        } catch (Exception e) {
            System.out.println("❌ No se cargó cintilla: " + e.getMessage());
        }

        pdf.close();
    }

    private PdfPCell celdaTitulo(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        return cell;
    }

    private PdfPCell celdaNormal(String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        return cell;
    }


}

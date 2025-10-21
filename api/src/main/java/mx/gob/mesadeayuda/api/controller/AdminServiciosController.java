package mx.gob.mesadeayuda.api.controller;

import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import mx.gob.mesadeayuda.api.model.DirectorioCorreo;
import mx.gob.mesadeayuda.api.repository.DirectorioCorreoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;


import jakarta.servlet.http.HttpServletResponse;
import com.itextpdf.text.BaseColor;




import com.itextpdf.text.Document;

import com.itextpdf.text.Font;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;

import com.itextpdf.text.pdf.PdfWriter;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminServiciosController {

    private final DirectorioCorreoRepository repo;

    // ============================================
    //   VISTA PRINCIPAL DE GESTIÓN DE SERVICIOS
    // ============================================
    @GetMapping("/gestion-servicios")
    public String vistaServicios(
            @RequestParam(value = "categoria", required = false, defaultValue = "correos") String categoria,
            @RequestParam(value = "area", required = false) String area,
            Model model) {

        model.addAttribute("categoria", categoria);

        // Lista de áreas desde BD
        List<String> areas = repo.obtenerAreas();
        model.addAttribute("areas", areas);

        // Si seleccionó un área, decodificarla (POR ESTO TRONABA)
        if (area != null && !area.isEmpty()) {

            String areaDecodificada = URLDecoder.decode(area, StandardCharsets.UTF_8);

            List<DirectorioCorreo> datos =
                    repo.findByAreaOrderByNombreCompletoAsc(areaDecodificada);

            model.addAttribute("datos", datos);
            model.addAttribute("areaSeleccionada", areaDecodificada);
        }

        return "admin_gservicios";
    }


    // ============================================
    //   ACTUALIZAR (EDICIÓN INLINE)
    // ============================================
    @PostMapping("/correos/actualizar")
    public String actualizarCorreo(
            @RequestParam Long id,
            @RequestParam String nombreCompleto,
            @RequestParam String correo,
            @RequestParam String extension,
            @RequestParam String puesto,
            @RequestParam String area
    ) {

        DirectorioCorreo d = repo.findById(id).orElse(null);

        if (d != null) {
            d.setNombreCompleto(nombreCompleto);
            d.setCorreo(correo);
            d.setExtension(extension);
            d.setPuesto(puesto);
            d.setArea(area);
            repo.save(d);
        }

        String areaEncoded = java.net.URLEncoder.encode(area, StandardCharsets.UTF_8);

        return "redirect:/admin/gestion-servicios?categoria=correos&area=" + areaEncoded;
    }

    // ============================================
    //   ELIMINAR REGISTRO
    // ============================================
    @PostMapping("/correos/eliminar/{id}")
    public String eliminarCorreo(@PathVariable Long id) {

        DirectorioCorreo d = repo.findById(id).orElse(null);
        String area = (d != null) ? d.getArea() : "";

        repo.deleteById(id);

        String areaEncoded = java.net.URLEncoder.encode(area, StandardCharsets.UTF_8);

        return "redirect:/admin/gestion-servicios?categoria=correos&area=" + areaEncoded;
    }

    @GetMapping("/correos/exportar-todo")
    public void exportarPDFCompleto(HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=directorio_correos.pdf");

        Document doc = new Document(PageSize.LETTER);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // ====== TÍTULO PRINCIPAL ======
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("DIRECTORIO DE CORREOS", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(15);
        doc.add(titulo);

        // Lista completa ordenada
        List<DirectorioCorreo> lista = repo.findAllByOrderByAreaAscNombreCompletoAsc();

        String areaActual = "";

        // ====== RECORRER TODAS LAS ÁREAS ======
        for (DirectorioCorreo d : lista) {

            // Detecta el cambio de área
            if (!d.getArea().equals(areaActual)) {
                areaActual = d.getArea();

                // ===== TÍTULO DEL ÁREA =====
                Font areaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
                Paragraph areaTitulo = new Paragraph("\n" + areaActual, areaFont);
                areaTitulo.setSpacingBefore(10);
                areaTitulo.setSpacingAfter(8);
                doc.add(areaTitulo);

                // ===== TABLA DE ENCABEZADO =====
                PdfPTable header = new PdfPTable(4);
                header.setWidthPercentage(100);
                header.setWidths(new float[]{25, 30, 15, 30});

                Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

                String[] cols = {"NOMBRE", "CORREO", "EXTENSIÓN", "PUESTO"};
                for (String c : cols) {
                    PdfPCell cell = new PdfPCell(new Phrase(c, headFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(new BaseColor(180, 20, 70)); // Vino GEM
                    cell.setPadding(6);
                    header.addCell(cell);
                }

                doc.add(header);
            }

            // ===== TABLA DE DATOS =====
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{25, 30, 15, 30});

            tabla.addCell(new PdfPCell(new Phrase(d.getNombreCompleto())));
            tabla.addCell(new PdfPCell(new Phrase(d.getCorreo())));
            tabla.addCell(new PdfPCell(new Phrase(d.getExtension())));
            tabla.addCell(new PdfPCell(new Phrase(d.getPuesto())));

            doc.add(tabla);
        }

        doc.close();
    }


}

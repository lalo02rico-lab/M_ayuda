package mx.gob.mesadeayuda.api.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mx.gob.mesadeayuda.api.dto.ResguardoUpdateDTO;
import mx.gob.mesadeayuda.api.model.*;
import mx.gob.mesadeayuda.api.repository.*;
import mx.gob.mesadeayuda.api.service.ResguardoCrudService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminServiciosController {

    private final VwResguardoRepository vwResguardoRepository;
    private final CorreoRepository correoRepository;
    private final LicenciaRepository licenciaRepository;
    private final ExtencionRepository extencionRepository;
    private final InventarioBienRepository inventarioBienRepository;

    private final ResguardoCrudService resguardoCrudService;


    /* =================================================
       FUENTES PDF
    ================================================= */
    private Font tituloFont() {
        return new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(123, 30, 61));
    }

    private Font headerFont() {
        return new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
    }

    private Font cellFont() {
        return new Font(Font.FontFamily.HELVETICA, 9);
    }

    private PdfPCell headerCell(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text, headerFont()));
        c.setBackgroundColor(new BaseColor(180, 137, 84));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

    private PdfPCell cell(String text) {
        PdfPCell c = new PdfPCell(new Phrase(text != null ? text : "", cellFont()));
        c.setHorizontalAlignment(Element.ALIGN_LEFT);
        return c;
    }

    @PostMapping("/resguardos/actualizar")
    public String actualizarResguardo(
            @RequestParam String tipoEquipo,
            @RequestParam Long id,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String usuarioFinal,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String observaciones
    ) {

        // 🔥 NORMALIZACIÓN
        tipoEquipo = tipoEquipo.trim();
        if (tipoEquipo.startsWith(",")) {
            tipoEquipo = tipoEquipo.substring(1);
        }

        ResguardoUpdateDTO d = new ResguardoUpdateDTO();
        d.setTipoEquipo(tipoEquipo);
        d.setId(id);
        d.setUsuario(usuario);
        d.setUsuarioFinal(usuarioFinal);
        d.setArea(area);
        d.setStatus(status);
        d.setObservaciones(observaciones);

        resguardoCrudService.actualizar(d);
        return "redirect:/admin/gestion-servicios?categoria=resguardos";
    }


    @PostMapping("/resguardos/liberar")
    @ResponseBody
    public void liberarResguardo(@RequestParam String tipoEquipo, @RequestParam Long id) {

        tipoEquipo = tipoEquipo.trim();
        if (tipoEquipo.startsWith(",")) {
            tipoEquipo = tipoEquipo.substring(1);
        }

        resguardoCrudService.liberar(tipoEquipo, id);
    }



    /* =================================================
       LISTADO + FILTROS + PAGINACIÓN
    ================================================= */
    @GetMapping("/gestion-servicios")
    public String gestionServicios(
            @RequestParam(defaultValue = "resguardos") String categoria,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, size);

        model.addAttribute("categoria", categoria);
        model.addAttribute("tipo", tipo);
        model.addAttribute("q", q);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        switch (categoria) {

            case "resguardos" -> {
                Page<VwResguardo> p =
                        (tipo != null || q != null)
                                ? vwResguardoRepository.filtrar(tipo, q, pageable)
                                : vwResguardoRepository.findAll(pageable);

                model.addAttribute("resguardos", p.getContent());
                model.addAttribute("totalPages", p.getTotalPages());
            }

            case "correos" -> {
                Page<Correo> p =
                        (q != null)
                                ? correoRepository.filtrar(q, pageable)
                                : correoRepository.findAll(pageable);

                model.addAttribute("correos", p.getContent());
                model.addAttribute("totalPages", p.getTotalPages());
            }

            case "licencias" -> {
                Page<Licencia> p =
                        (q != null)
                                ? licenciaRepository.filtrar(q, pageable)
                                : licenciaRepository.findAll(pageable);

                model.addAttribute("licencias", p.getContent());
                model.addAttribute("totalPages", p.getTotalPages());
            }

            case "extenciones" -> {
                Page<Extencion> p =
                        (q != null)
                                ? extencionRepository.filtrar(q, pageable)
                                : extencionRepository.findAll(pageable);

                model.addAttribute("extenciones", p.getContent());
                model.addAttribute("totalPages", p.getTotalPages());
            }

            case "inventarios" -> {
                Page<InventarioBien> p =
                        (q != null)
                                ? inventarioBienRepository.filtrar(q, pageable)
                                : inventarioBienRepository.findAll(pageable);

                model.addAttribute("inventarios", p.getContent());
                model.addAttribute("totalPages", p.getTotalPages());
            }
        }

        return "admin_gservicios";
    }

    /* =================================================
       OBTENER (MODAL EDITAR)
    ================================================= */
    @GetMapping("/obtener/{categoria}/{id}")
    @ResponseBody
    public Object obtener(@PathVariable String categoria, @PathVariable String id) {

        return switch (categoria) {
            case "correos" -> correoRepository.findById(Long.valueOf(id)).orElse(null);
            case "licencias" -> licenciaRepository.findById(Long.valueOf(id)).orElse(null);
            case "extenciones" -> extencionRepository.findById(Long.valueOf(id)).orElse(null);
            case "inventarios" -> inventarioBienRepository.findByInventario(id);
            default -> null;
        };
    }

    /* =================================================
       GUARDAR
    ================================================= */
    @PostMapping("/guardar")
    public String guardar(
            @RequestParam String categoria,
            @RequestParam(required = false) Long id,
            HttpServletRequest request
    ) {

        if ("correos".equals(categoria)) {
            Correo c = (id != null)
                    ? correoRepository.findById(id).orElse(new Correo())
                    : new Correo();

            c.setUsuario(request.getParameter("usuario"));
            c.setCorreo(request.getParameter("correo"));
            c.setTipoLicencia(request.getParameter("tipoLicencia"));
            c.setArea(request.getParameter("area"));
            correoRepository.save(c);
        }

        if ("licencias".equals(categoria)) {
            Licencia l = (id != null)
                    ? licenciaRepository.findById(id).orElse(new Licencia())
                    : new Licencia();

            l.setUsuario(request.getParameter("usuario"));
            l.setArea(request.getParameter("area"));
            l.setTotalLicencias(Integer.valueOf(request.getParameter("totalLicencias")));
            licenciaRepository.save(l);
        }

        if ("extenciones".equals(categoria)) {

            //  La extensión ES el ID (viene del input "extension")
            String ext = request.getParameter("extension");

            if (ext == null || ext.isBlank()) {
                throw new IllegalArgumentException("La extensión es obligatoria");
            }

            Long extension = Long.valueOf(ext);

            // Si existe → edita | si no → crea
            Extencion e = extencionRepository.findById(extension)
                    .orElse(new Extencion());

            //  ASIGNAR ID ANTES DE GUARDAR (OBLIGATORIO)
            e.setId(extension);

            e.setNombre(request.getParameter("nombre"));
            e.setDepartamento(request.getParameter("departamento"));
            e.setCorreo(request.getParameter("correo"));

            extencionRepository.save(e);
        }


        if ("inventarios".equals(categoria)) {
            String inv = request.getParameter("inventario");
            InventarioBien i = inventarioBienRepository.findByInventario(inv);

            if (i == null) {
                i = new InventarioBien();
                i.setInventario(inv);
            }

            i.setNic(request.getParameter("nic"));
            i.setDescripcionBien(request.getParameter("descripcionBien"));

            String val = request.getParameter("valor");
            if (val != null && !val.isBlank()) {
                i.setValor(new BigDecimal(val));
            }

            i.setMarca(request.getParameter("marca"));
            i.setModelo(request.getParameter("modelo"));
            i.setSerie(request.getParameter("serie"));
            i.setClaveDependencia(request.getParameter("claveDependencia"));
            i.setNombreDependencia(request.getParameter("nombreDependencia"));
            i.setResguardatario(request.getParameter("resguardatario"));
            i.setNombreInmueble(request.getParameter("nombreInmueble"));
            i.setProveedor(request.getParameter("proveedor"));
            i.setEstadoUso(request.getParameter("estadoUso"));
            i.setSalMinUma(request.getParameter("salMinUma"));
            i.setTipoPropiedad(request.getParameter("tipoPropiedad"));
            i.setFormaAdquisicion(request.getParameter("formaAdquisicion"));
            i.setTipoDocumento(request.getParameter("tipoDocumento"));
            i.setCaracteristicas(request.getParameter("caracteristicas"));
            i.setMaterial(request.getParameter("material"));
            i.setColor(request.getParameter("color"));
            i.setTipoAsignacion(request.getParameter("tipoAsignacion"));
            i.setPlacas(request.getParameter("placas"));
            i.setActivoGenerico(request.getParameter("activoGenerico"));
            i.setGrupoActivo(request.getParameter("grupoActivo"));
            i.setActivoEspecifico(request.getParameter("activoEspecifico"));

            DateTimeFormatter fIso = DateTimeFormatter.ISO_DATE;

            if (!request.getParameter("fechaAdquisicion").isBlank())
                i.setFechaAdquisicion(LocalDate.parse(request.getParameter("fechaAdquisicion"), fIso));
            if (!request.getParameter("fechaAsignacion").isBlank())
                i.setFechaAsignacion(LocalDate.parse(request.getParameter("fechaAsignacion"), fIso));
            if (!request.getParameter("fechaFirmaResg").isBlank())
                i.setFechaFirmaResg(LocalDate.parse(request.getParameter("fechaFirmaResg"), fIso));

            inventarioBienRepository.save(i);
        }

        return "redirect:/admin/gestion-servicios?categoria=" + categoria;
    }

    /* =================================================
       ELIMINAR
    ================================================= */
    @PostMapping("/eliminar")
    @ResponseBody
    public void eliminar(@RequestParam String categoria, @RequestParam String id) {

        switch (categoria) {
            case "correos" -> correoRepository.deleteById(Long.valueOf(id));
            case "licencias" -> licenciaRepository.deleteById(Long.valueOf(id));
            case "extenciones" -> extencionRepository.deleteById(Long.valueOf(id));
            case "inventarios" -> inventarioBienRepository.deleteById(Long.valueOf(id));

        }
    }

    @PostMapping("/resguardos/agregar")
    public String agregarResguardo(HttpServletRequest request) {

        resguardoCrudService.agregar(request);

        return "redirect:/admin/gestion-servicios?categoria=resguardos";
    }



    /* =================================================
       DESCARGAR PDF (COMPLETO)
    ================================================= */
    @GetMapping("/descargar-pdf")
    public void descargarPdf(
            @RequestParam String categoria,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String q,
            HttpServletResponse response
    ) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + categoria + ".pdf");

        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        Paragraph titulo = new Paragraph(
                categoria.toUpperCase() + " - MESA DE AYUDA",
                tituloFont()
        );
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(15);
        doc.add(titulo);

        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        /* ================= RESGUARDOS ================= */
        if ("resguardos".equals(categoria)) {

            List<VwResguardo> lista =
                    (tipo != null || q != null)
                            ? vwResguardoRepository.filtrar(tipo, q)
                            : vwResguardoRepository.findAll();

            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);

            String[] headers = {"No","Tipo","Usuario","Usuario Final","Dirección","Área","Perfil","Modelo","Serie","Status"};
            for (String h : headers) table.addCell(headerCell(h));

            for (VwResguardo r : lista) {
                table.addCell(cell(r.getNo().toString()));
                table.addCell(cell(r.getTipoEquipo()));
                table.addCell(cell(r.getUsuario()));
                table.addCell(cell(r.getUsuarioFinal()));
                table.addCell(cell(r.getDireccion()));
                table.addCell(cell(r.getArea()));
                table.addCell(cell(r.getPerfilEquipo()));
                table.addCell(cell(r.getModelo()));
                table.addCell(cell(r.getSerieCpu()));
                table.addCell(cell(r.getStatus()));
            }
            doc.add(table);
        }

        /* ================= CORREOS ================= */
        if ("correos".equals(categoria)) {

            List<Correo> lista =
                    (q != null)
                            ? correoRepository.filtrar(q)
                            : correoRepository.findAll();

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(80);

            String[] headers = {"Usuario", "Correo", "Licencia", "Área"};
            for (String h : headers) table.addCell(headerCell(h));

            for (Correo c : lista) {
                table.addCell(cell(c.getUsuario()));
                table.addCell(cell(c.getCorreo()));
                table.addCell(cell(c.getTipoLicencia()));
                table.addCell(cell(c.getArea()));
            }
            doc.add(table);
        }

        /* ================= LICENCIAS ================= */
        if ("licencias".equals(categoria)) {

            List<Licencia> lista =
                    (q != null)
                            ? licenciaRepository.filtrar(q)
                            : licenciaRepository.findAll();

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(60);

            String[] headers = {"Usuario", "Área", "Total Licencias"};
            for (String h : headers) table.addCell(headerCell(h));

            for (Licencia l : lista) {
                table.addCell(cell(l.getUsuario()));
                table.addCell(cell(l.getArea()));
                table.addCell(cell(String.valueOf(l.getTotalLicencias())));
            }
            doc.add(table);
        }


        /* ================= EXTENCIONES ================= */
        if ("extenciones".equals(categoria)) {

            List<Extencion> lista =
                    (q != null && !q.isBlank())
                            ? extencionRepository.filtrar(q)
                            : extencionRepository.findAll();

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(70);
            table.setSpacingBefore(10f);

            String[] headers = {"Extensión", "Nombre", "Departamento", "Correo"};
            for (String h : headers) {
                table.addCell(headerCell(h));
            }

            for (Extencion e : lista) {
                table.addCell(cell(e.getId() != null ? e.getId().toString() : ""));
                table.addCell(cell(e.getNombre()));
                table.addCell(cell(e.getDepartamento()));
                table.addCell(cell(e.getCorreo()));
            }

            doc.add(table);
        }


        /* ================= INVENTARIOS ================= */
        if ("inventarios".equals(categoria)) {

            for (InventarioBien i : inventarioBienRepository.findAll()) {

                Paragraph t = new Paragraph(
                        "INVENTARIO: " + i.getInventario(),
                        tituloFont()
                );
                t.setSpacingBefore(10);
                t.setSpacingAfter(10);
                doc.add(t);

                PdfPTable ficha = new PdfPTable(4);
                ficha.setWidthPercentage(100);
                ficha.setWidths(new float[]{20, 30, 20, 30});

                ficha.addCell(headerCell("NIC"));
                ficha.addCell(cell(i.getNic()));
                ficha.addCell(headerCell("Descripción"));
                ficha.addCell(cell(i.getDescripcionBien()));

                ficha.addCell(headerCell("Marca"));
                ficha.addCell(cell(i.getMarca()));
                ficha.addCell(headerCell("Modelo"));
                ficha.addCell(cell(i.getModelo()));

                ficha.addCell(headerCell("Serie"));
                ficha.addCell(cell(i.getSerie()));
                ficha.addCell(headerCell("Valor"));
                ficha.addCell(cell(i.getValor() != null ? "$ " + i.getValor() : ""));

                ficha.addCell(headerCell("Dependencia"));
                ficha.addCell(cell(i.getNombreDependencia()));
                ficha.addCell(headerCell("Clave Dependencia"));
                ficha.addCell(cell(i.getClaveDependencia()));

                ficha.addCell(headerCell("Resguardatario"));
                ficha.addCell(cell(i.getResguardatario()));
                ficha.addCell(headerCell("Inmueble"));
                ficha.addCell(cell(i.getNombreInmueble()));

                ficha.addCell(headerCell("Fecha Adquisición"));
                ficha.addCell(cell(i.getFechaAdquisicion() != null ? i.getFechaAdquisicion().format(f) : ""));
                ficha.addCell(headerCell("Forma Adquisición"));
                ficha.addCell(cell(i.getFormaAdquisicion()));

                ficha.addCell(headerCell("Tipo Documento"));
                ficha.addCell(cell(i.getTipoDocumento()));
                ficha.addCell(headerCell("UMA"));
                ficha.addCell(cell(i.getSalMinUma()));

                ficha.addCell(headerCell("Material"));
                ficha.addCell(cell(i.getMaterial()));
                ficha.addCell(headerCell("Color"));
                ficha.addCell(cell(i.getColor()));

                ficha.addCell(headerCell("Características"));
                ficha.addCell(cell(i.getCaracteristicas()));
                ficha.addCell(headerCell("Estado Uso"));
                ficha.addCell(cell(i.getEstadoUso()));

                ficha.addCell(headerCell("Tipo Asignación"));
                ficha.addCell(cell(i.getTipoAsignacion()));
                ficha.addCell(headerCell("Placas"));
                ficha.addCell(cell(i.getPlacas()));

                ficha.addCell(headerCell("Activo Genérico"));
                ficha.addCell(cell(i.getActivoGenerico()));
                ficha.addCell(headerCell("Grupo Activo"));
                ficha.addCell(cell(i.getGrupoActivo()));

                ficha.addCell(headerCell("Activo Específico"));
                ficha.addCell(cell(i.getActivoEspecifico()));
                ficha.addCell(new PdfPCell());
                ficha.addCell(new PdfPCell());

                doc.add(ficha);
                doc.newPage();
            }
        }

        doc.close();
    }
}

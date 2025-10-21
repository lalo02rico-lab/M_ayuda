package mx.gob.mesadeayuda.api.controller;

import mx.gob.mesadeayuda.api.dto.AuthRequest;
import mx.gob.mesadeayuda.api.model.Directorio;
import mx.gob.mesadeayuda.api.model.Usuario;
import mx.gob.mesadeayuda.api.repository.DirectorioRepository;
import mx.gob.mesadeayuda.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DirectorioController {

    @Autowired
    private DirectorioRepository directorioRepository;

    // 🔹 AGREGADO: repositorio de usuarios (NO reemplaza nada)
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/directorio")
    public String mostrarDirectorio(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String filtro,
            @ModelAttribute("mensaje") String mensaje
    ) {
        Pageable pageable = PageRequest.of(page, 7);
        Page<Directorio> pagina;

        if (filtro != null && !filtro.isEmpty()) {
            filtro = "%" + filtro.toUpperCase() + "%";
            pagina = directorioRepository.findByFiltro(filtro, pageable);
        } else {
            pagina = directorioRepository.findAll(pageable);
        }

        int totalPages = pagina.getTotalPages();
        int start = Math.max(0, page - 1);
        int end = Math.min(start + 2, totalPages - 1);

        model.addAttribute("contactos", pagina.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("filtro", filtro != null ? filtro.replace("%", "") : "");
        model.addAttribute("startPage", start);
        model.addAttribute("endPage", end);

        if (mensaje != null && !mensaje.isBlank()) {
            model.addAttribute("mensaje", mensaje);
        }

        return "directorio";
    }

    // 🔹 Mostrar formulario de edición
    @GetMapping("/directorio/editar/{id}")
    public String editarContacto(@PathVariable("id") Long id, Model model) {
        Directorio contacto = directorioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Id de directorio inválido: " + id));

        model.addAttribute("contacto", contacto);
        return "directorio-editar";
    }

    // 🔹 Procesar actualización
    @PostMapping("/directorio/actualizar")
    public String actualizarContacto(@ModelAttribute("contacto") Directorio contacto,
                                     RedirectAttributes redirectAttributes) {

        directorioRepository.save(contacto);
        redirectAttributes.addFlashAttribute("mensaje", "Registro actualizado correctamente.");

        return "redirect:/directorio";
    }

    // ────────────────────────────────────────────────
    // 🔥 NUEVO: Validar ADMIN con credenciales reales
    // ────────────────────────────────────────────────
    @PostMapping("/validar-admin")
    @ResponseBody
    public Map<String, Object> validarAdmin(@RequestBody AuthRequest auth) {

        Map<String, Object> resp = new HashMap<>();

        // Buscar usuario por correo
        Usuario u = usuarioRepository.findByCorreo(auth.getCorreo());

        // Validar credenciales + rol
        if (u != null &&
                u.getRol() != null &&
                u.getRol().getNombre().equalsIgnoreCase("Administrador") &&
                u.getContrasena().equals(auth.getContrasena())) {

            resp.put("autorizado", true);

        } else {
            resp.put("autorizado", false);
        }

        return resp;
    }
}

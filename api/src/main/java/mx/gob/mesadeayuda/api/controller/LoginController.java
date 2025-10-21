package mx.gob.mesadeayuda.api.controller;

import jakarta.servlet.http.HttpSession;
import mx.gob.mesadeayuda.api.model.Usuario;
import mx.gob.mesadeayuda.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // === Mostrar la vista del login ===
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // === Procesar inicio de sesión ===
    @PostMapping("/login")
    public String iniciarSesion(
            @RequestParam("correo") String correo,
            @RequestParam("contrasena") String contrasena,
            HttpSession session,
            Model model) {

        Usuario usuario = usuarioRepository.findByCorreoAndContrasena(correo, contrasena);

        if (usuario == null) {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login";
        }

        // Guardar el usuario en sesión
        session.setAttribute("usuario", usuario);

        // Obtener el nombre del rol desde la entidad Rol
        String rol = usuario.getRol().getNombre();

        // Redirigir según el tipo de usuario
        if (rol.equalsIgnoreCase("ADMINISTRADOR")) {
            return "redirect:/admin/menu";   // ✅ URL correcta
        } else if (rol.equalsIgnoreCase("TECNICO")) {
            return "redirect:/tecnico/menu?idTecnico=" + usuario.getIdUsuario();
        }


        // Si no tiene rol válido, regresar al login
        model.addAttribute("error", "Rol no autorizado");
        session.invalidate();
        return "login";
    }

    // === Cerrar sesión ===
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); // elimina los datos de sesión
        return "redirect:/login"; // redirige al login
    }
}

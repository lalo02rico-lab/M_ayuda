package mx.gob.mesadeayuda.api.controller;

import mx.gob.mesadeayuda.api.model.Usuario;
import mx.gob.mesadeayuda.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- Función para normalizar textos (quita acentos, espacios, mayúsculas)
    private String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // quita acentos
                .trim()
                .toLowerCase();
    }

    // Mostrar el login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // templates/login.html
    }

    // Procesar el login
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam String correo, // <<-- CAMBIADO: Recibe 'correo' en lugar de 'username'
            @RequestParam String password,
            @RequestParam String rol,
            Model model) {

        // CAMBIADO: Usar el nuevo método del repositorio findByCorreoAndContrasena
        Optional<Usuario> usuarioOpt =
                usuarioRepository.findByCorreoAndContrasena(correo, password);
        System.out.println("DEBUG: Buscando usuario con Correo='" + correo + "' y Contraseña='" + password + "'");

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String rolBD = usuario.getRol() != null ? usuario.getRol().getNombre() : "SIN ROL";

            System.out.println("Desde BD: user=" + usuario.getNombreUsuario()
                    + " | pass=" + usuario.getContrasena()
                    + " | rolBD=" + rolBD
                    + " | rolForm=" + rol);

            // Compara normalizando
            if (!normalizar(rolBD).equals(normalizar(rol))) {
                model.addAttribute("error", "El rol seleccionado no corresponde al usuario. (BD=" + rolBD + ")");
                return "login";
            }

            model.addAttribute("usuario", usuario);

            // Redirección según rol normalizado
            String rolNorm = normalizar(rolBD);
            if ("administrador".equals(rolNorm)) {
                return "menu_admin";
            } else if ("tecnico".equals(rolNorm)) {
                return "menu_tecnico";
            } else {
                return "index"; // Servidor Público
            }

        } else {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "login";
        }
    }
}
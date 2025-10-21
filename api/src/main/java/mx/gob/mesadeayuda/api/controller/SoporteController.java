package mx.gob.mesadeayuda.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import mx.gob.mesadeayuda.api.model.CatDepartamento;
import mx.gob.mesadeayuda.api.model.PreSolicitud;
import mx.gob.mesadeayuda.api.repository.CatDepartamentoRepository;
import mx.gob.mesadeayuda.api.repository.PreSolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class SoporteController {

    @Autowired
    private CatDepartamentoRepository departamentoRepository;

    @Autowired
    private PreSolicitudRepository preSolicitudRepository;

    /**
     * 🧩 Vista inicial (ventana emergente de registro)
     * Carga los departamentos desde la base de datos.
     */
    @GetMapping("/solicitud_soporte")
    public String mostrarFormulario(Model model) {
        model.addAttribute("departamentos", departamentoRepository.findAll());
        return "solicitud_soporte";  // HTML dentro de /templates/
    }

    /**
     * 💾 Guarda la pre-solicitud (nombre + departamento)
     * en la tabla PRE_SOLICITUD y redirige al menú de categorías.
     */
    @PostMapping("/guardar_registro")
    public String guardarRegistro(@RequestParam("nombre") String nombre,
                                  @RequestParam("id_departamento") Long idDepartamento,
                                  Model model) {

        // Busca el departamento seleccionado
        CatDepartamento departamento = departamentoRepository.findById(idDepartamento).orElse(null);

        // Crea una nueva pre-solicitud y guarda en BD
        PreSolicitud preSolicitud = new PreSolicitud();
        preSolicitud.setNombre(nombre);
        preSolicitud.setDepartamento(departamento);
        preSolicitudRepository.save(preSolicitud);

        // Pasa los datos a la vista siguiente
        model.addAttribute("nombre", nombre);
        model.addAttribute("departamento", departamento.getNombreDepartamento());

        return "categorias_soporte"; // Redirige al menú de categorías
    }

    /**
     * 📋 Muestra el formulario del tipo de soporte elegido (Ofimática, Correo, etc.)
     */
    @GetMapping("/categoria/{tipo}")
    public String mostrarFormularioCategoria(@PathVariable String tipo,
                                             @RequestParam(required = false) String nombre,
                                             @RequestParam(required = false) String departamento,
                                             Model model) {

        // Datos básicos del usuario y tipo de soporte
        model.addAttribute("tipo", tipo.toUpperCase());
        model.addAttribute("nombre", nombre);
        model.addAttribute("departamento", departamento);

        return "formulario_soporte";
    }

    /**
     * 🎟️ Muestra el ticket generado al finalizar el proceso de solicitud
     */
    @PostMapping("/ticket_generado")
    public String mostrarTicketGenerado(@RequestParam("descripcion") String descripcion,
                                        @RequestParam("fechaHora") String fechaHora,
                                        @RequestParam(value = "nombre", required = false) String nombre,
                                        @RequestParam(value = "departamento", required = false) String departamento,
                                        Model model) {

        // Datos simulados (después se insertará en la tabla TICKETS)
        model.addAttribute("fechaHora", fechaHora);
        model.addAttribute("nombre", nombre);
        model.addAttribute("departamento", departamento);
        model.addAttribute("descripcion", descripcion);

        return "ticket_generado";
    }
}

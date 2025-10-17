package mx.gob.mesadeayuda.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SoporteController {

    @GetMapping("/solicitud_soporte")
    public String mostrarFormulario() {
        return "solicitud_soporte";
    }

    @PostMapping("/guardar-registro")
    public String guardarRegistro(@RequestParam("nombre") String nombre,
                                  @RequestParam("departamento") String departamento,
                                  Model model) {

        // Por ahora solo simulamos el guardado
        model.addAttribute("nombre", nombre);
        model.addAttribute("departamento", departamento);


        // Aquí después se insertará en la base de datos
        return "categorias_soporte"; // redirige al menú de categorías
    }

    //  NUEVA RUTA: para mostrar el formulario según categoría
    @GetMapping("/categoria/{tipo}")
    public String mostrarFormularioCategoria(@PathVariable String tipo,
                                             @RequestParam(required = false) String nombre,
                                             @RequestParam(required = false) String departamento,
                                             Model model) {

        // Carga los datos básicos para mostrar
        model.addAttribute("tipo", tipo.toUpperCase());
        model.addAttribute("nombre", nombre != null ? nombre : "...........");
        model.addAttribute("departamento", departamento != null ? departamento : "...........");

        return "formulario_soporte";
    }


@PostMapping("/ticket_generado")
public String mostrarTicketGenerado(@RequestParam("descripcion") String descripcion,
                                    @RequestParam("fechaHora") String fechaHora,
                                    @RequestParam(value = "nombre", required = false) String nombre,
                                    @RequestParam(value = "departamento", required = false) String departamento,
                                    Model model) {

    // Simulación de datos (luego se reemplazará con los datos reales)
    model.addAttribute("fechaHora", fechaHora);
    model.addAttribute("nombre", nombre != null ? nombre : "Servidor Público");
    model.addAttribute("departamento", departamento != null ? departamento : "Área perteneciente");
    model.addAttribute("descripcion", descripcion);

    return "ticket_generado";
}
}






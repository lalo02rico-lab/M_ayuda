package mx.gob.mesadeayuda.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // Página principal (index.html)
    }

    @GetMapping("/organigrama")
    public String organigrama() {
        return "organigrama"; // Carga organigrama.html desde templates
    }



    @GetMapping("/manuales")
    public String manuales() {
        return "manuales"; // Carga manuales.html
    }

    @GetMapping("/preguntas")
    public String preguntas() {
        return "preguntas"; // Carga preguntas.html
    }

}

package mx.gob.mesadeayuda.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        // Devuelve el archivo templates/index.html
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        // Devuelve el archivo templates/login.html (cuando lo tengamos)
        return "login";
    }

    @GetMapping("/menu")
    public String menu() {
        // Devuelve el archivo templates/menu.html (cuando lo hagamos)
        return "menu";
    }
}

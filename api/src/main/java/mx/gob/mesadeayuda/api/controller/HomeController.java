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

   //@GetMapping("/login")
    //public String login(){
      //  return "login"; // buscará templates/login.html
    //}


    @GetMapping("/menu")
    public String menu() {
        // Devuelve el archivo templates/menu.html (cuando lo hagamos)
        return "menu";
    }
    @GetMapping("/organigrama")
    public String organigrama() {
        return "organigrama"; // busca templates/organigrama.html
    }
    //@GetMapping("/directorio")
    //public String directorio() {
      //  return "directorio"; // templates/directorio.html
    }



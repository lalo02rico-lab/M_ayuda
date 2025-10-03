package mx.gob.mesadeayuda.api.controller;

import mx.gob.mesadeayuda.api.model.Directorio;
import mx.gob.mesadeayuda.api.repository.DirectorioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DirectorioController {

    private final DirectorioRepository directorioRepository;

    public DirectorioController(DirectorioRepository directorioRepository) {
        this.directorioRepository = directorioRepository;
    }

    @GetMapping("/directorio")
    public String mostrarDirectorio(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size, //  7 registros por página
            @RequestParam(defaultValue = "") String dependencia,
            @RequestParam(defaultValue = "") String profesion,
            @RequestParam(defaultValue = "") String nombre,
            @RequestParam(defaultValue = "") String cargo,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Directorio> pagina = directorioRepository
                .findByDependenciaContainingIgnoreCaseAndProfesionContainingIgnoreCaseAndNombreContainingIgnoreCaseAndCargoContainingIgnoreCase(
                        dependencia, profesion, nombre, cargo, pageable);

        model.addAttribute("pagina", pagina);
        model.addAttribute("dependencia", dependencia);
        model.addAttribute("profesion", profesion);
        model.addAttribute("nombre", nombre);
        model.addAttribute("cargo", cargo);

        return "directorio";
    }
}

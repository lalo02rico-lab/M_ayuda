package mx.gob.mesadeayuda.api.repository;

import mx.gob.mesadeayuda.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByCorreoAndContrasena(String correo, String contrasena);

    List<Usuario> findByRolNombreIgnoreCase(String nombre);

    //NECESARIO para validar admin por correo
    Usuario findByCorreo(String correo);

}

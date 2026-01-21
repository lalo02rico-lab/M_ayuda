package mx.gob.mesadeayuda.api.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mx.gob.mesadeayuda.api.dto.ResguardoUpdateDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ResguardoCrudService {

    private final JdbcTemplate jdbc;

    private String tablaPorTipo(String tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("TIPO_EQUIPO es null");
        }
        return switch (tipo) {
            case "ESCRITORIO_BASICO" -> "ESCRITORIO_BASICO";
            case "MOVIL_AVANZADO" -> "MOVIL_AVANZADO";
            case "ESCRITORIO_AVANZADO" -> "ESCRITORIO_AVANZADO";
            case "MOVIL_MEDIO" -> "MOVIL_MEDIO";
            case "PORTATIL_DONADO" -> "PORTATIL_DONADO";
            default -> throw new IllegalArgumentException("TIPO_EQUIPO inválido: " + tipo);
        };
    }

    /* =================================================
       AGREGAR (TODOS LOS TIPOS)
    ================================================= */
    public void agregar(HttpServletRequest r) {

        String tipo = r.getParameter("tipoEquipo");
        String tabla = tablaPorTipo(tipo);

        switch (tabla) {

            /* ===============================
               ESCRITORIO BÁSICO
            =============================== */
            case "ESCRITORIO_BASICO" -> jdbc.update("""
                INSERT INTO ESCRITORIO_BASICO
                (NO, USUARIO, DIRECCION, SUBDIRECCION, PERFIL_EQUIPO,
                 MODELO_CPU, SERIE_CPU, MODELO_MONITOR, SERIE_MONITOR,
                 SERIE_UPS, SERIE_TECLADO, SERIE_MOUSE, STATUS, OBSERVACIONES)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """,
                    Integer.valueOf(r.getParameter("no")),
                    r.getParameter("usuario"),
                    r.getParameter("direccion"),
                    r.getParameter("area"),
                    r.getParameter("perfilEquipo"),
                    r.getParameter("modelo"),
                    r.getParameter("serieCpu"),
                    r.getParameter("modeloMonitor"),
                    r.getParameter("serieMonitor"),
                    r.getParameter("serieUps"),
                    r.getParameter("serieTeclado"),
                    r.getParameter("serieMouse"),
                    r.getParameter("status"),
                    r.getParameter("observaciones")
            );

            /* ===============================
               MÓVIL AVANZADO
            =============================== */
            case "MOVIL_AVANZADO" -> jdbc.update("""
                INSERT INTO MOVIL_AVANZADO
                (NO, USUARIO, USUARIO_FINAL, DIRECCION, AREA_ADSCRIPCION,
                 PERFIL_EQUIPO, MODELO_CPU, SERIE_CPU, STATUS, OBSERVACIONES)
                VALUES (?,?,?,?,?,?,?,?,?,?)
            """,
                    Integer.valueOf(r.getParameter("no")),
                    r.getParameter("usuario"),
                    r.getParameter("usuarioFinal"),
                    r.getParameter("direccion"),
                    r.getParameter("area"),
                    r.getParameter("perfilEquipo"),
                    r.getParameter("modelo"),
                    r.getParameter("serieCpu"),
                    r.getParameter("status"),
                    r.getParameter("observaciones")
            );

            /* ===============================
               ESCRITORIO AVANZADO
            =============================== */
            case "ESCRITORIO_AVANZADO" -> jdbc.update("""
                INSERT INTO ESCRITORIO_AVANZADO
                (NO, USUARIO, USUARIO_FINAL, DIRECCION, SUB_DIRECCION,
                 PERFIL_DEL_EQUIPO, MODELO, NUMERO_SERIE_CPU,
                 NUMERO_SERIE_MONITOR, NUMERO_SERIE_UPS,
                 NUMERO_SERIE_TECLADO, NUMERO_SERIE_MOUSE,
                 STATUS, OBSERVACIONES)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """,
                    Integer.valueOf(r.getParameter("no")),
                    r.getParameter("usuario"),
                    r.getParameter("usuarioFinal"),
                    r.getParameter("direccion"),
                    r.getParameter("area"),
                    r.getParameter("perfilEquipo"),
                    r.getParameter("modelo"),
                    r.getParameter("serieCpu"),
                    r.getParameter("serieMonitor"),
                    r.getParameter("serieUps"),
                    r.getParameter("serieTeclado"),
                    r.getParameter("serieMouse"),
                    r.getParameter("status"),
                    r.getParameter("observaciones")
            );

            /* ===============================
               MÓVIL MEDIO
            =============================== */
            case "MOVIL_MEDIO" -> jdbc.update("""
                INSERT INTO MOVIL_MEDIO
                (NO, USUARIO, DIRECCION, SUB_DIRECCION,
                 PERFIL_DEL_EQUIPO, MODELO,
                 NUMERO_DE_SERIE, STATUS, OBSERVACIONES)
                VALUES (?,?,?,?,?,?,?,?,?)
            """,
                    Integer.valueOf(r.getParameter("no")),
                    r.getParameter("usuario"),
                    r.getParameter("direccion"),
                    r.getParameter("area"),
                    r.getParameter("perfilEquipo"),
                    r.getParameter("modelo"),
                    r.getParameter("serieCpu"),
                    r.getParameter("status"),
                    r.getParameter("observaciones")
            );

            /* ===============================
               PORTÁTIL DONADO
            =============================== */
            case "PORTATIL_DONADO" -> jdbc.update("""
                INSERT INTO PORTATIL_DONADO
                (NO, USUARIO, DIRECCION, AREA_ADSCRIPCION,
                 PERFIL_DEL_EQUIPO, MODELO,
                 NUMERO_SERIE_CPU, STATUS, OBSERVACIONES)
                VALUES (?,?,?,?,?,?,?,?,?)
            """,
                    Integer.valueOf(r.getParameter("no")),
                    r.getParameter("usuario"),
                    r.getParameter("direccion"),
                    r.getParameter("area"),
                    r.getParameter("perfilEquipo"),
                    r.getParameter("modelo"),
                    r.getParameter("serieCpu"),
                    r.getParameter("status"),
                    r.getParameter("observaciones")
            );
        }
    }

    /* =================================================
       EDITAR (YA FUNCIONABA)
    ================================================= */
    public void actualizar(ResguardoUpdateDTO d) {

        String tabla = tablaPorTipo(d.getTipoEquipo());

        String usuario = StringUtils.hasText(d.getUsuario()) ? d.getUsuario().trim() : null;
        String usuarioFinal = StringUtils.hasText(d.getUsuarioFinal()) ? d.getUsuarioFinal().trim() : null;
        String area = StringUtils.hasText(d.getArea()) ? d.getArea().trim() : null;
        String status = StringUtils.hasText(d.getStatus()) ? d.getStatus().trim() : null;
        String obs = StringUtils.hasText(d.getObservaciones()) ? d.getObservaciones().trim() : null;

        switch (tabla) {

            case "ESCRITORIO_BASICO" ->
                    jdbc.update("""
                    UPDATE ESCRITORIO_BASICO
                    SET USUARIO=?, SUBDIRECCION=?, STATUS=?, OBSERVACIONES=?
                    WHERE ID=?
                """, usuario, area, status, obs, d.getId());

            case "MOVIL_AVANZADO" ->
                    jdbc.update("""
                    UPDATE MOVIL_AVANZADO
                    SET USUARIO=?, USUARIO_FINAL=?, AREA_ADSCRIPCION=?, STATUS=?, OBSERVACIONES=?
                    WHERE ID=?
                """, usuario, usuarioFinal, area, status, obs, d.getId());

            case "ESCRITORIO_AVANZADO" ->
                    jdbc.update("""
                    UPDATE ESCRITORIO_AVANZADO
                    SET USUARIO=?, USUARIO_FINAL=?, SUB_DIRECCION=?, STATUS=?, OBSERVACIONES=?
                    WHERE ID=?
                """, usuario, usuarioFinal, area, status, obs, d.getId());

            case "MOVIL_MEDIO" ->
                    jdbc.update("""
                    UPDATE MOVIL_MEDIO
                    SET USUARIO=?, SUB_DIRECCION=?, STATUS=?, OBSERVACIONES=?
                    WHERE ID=?
                """, usuario, area, status, obs, d.getId());

            case "PORTATIL_DONADO" ->
                    jdbc.update("""
                    UPDATE PORTATIL_DONADO
                    SET USUARIO=?, AREA_ADSCRIPCION=?, STATUS=?, OBSERVACIONES=?
                    WHERE ID=?
                """, usuario, area, status, obs, d.getId());
        }
    }

    /* =================================================
       LIBERAR (YA FUNCIONABA)
    ================================================= */
    public void liberar(String tipoEquipo, Long id) {

        String tabla = tablaPorTipo(tipoEquipo);

        switch (tabla) {
            case "ESCRITORIO_BASICO" ->
                    jdbc.update("UPDATE ESCRITORIO_BASICO SET USUARIO=NULL, STATUS='LIBRE' WHERE ID=?", id);

            case "MOVIL_AVANZADO" ->
                    jdbc.update("UPDATE MOVIL_AVANZADO SET USUARIO=NULL, USUARIO_FINAL=NULL, STATUS='LIBRE' WHERE ID=?", id);

            case "ESCRITORIO_AVANZADO" ->
                    jdbc.update("UPDATE ESCRITORIO_AVANZADO SET USUARIO=NULL, USUARIO_FINAL=NULL, STATUS='LIBRE' WHERE ID=?", id);

            case "MOVIL_MEDIO" ->
                    jdbc.update("UPDATE MOVIL_MEDIO SET USUARIO=NULL, STATUS='LIBRE' WHERE ID=?", id);

            case "PORTATIL_DONADO" ->
                    jdbc.update("UPDATE PORTATIL_DONADO SET USUARIO=NULL, STATUS='LIBRE' WHERE ID=?", id);
        }
    }
}

package mx.gob.mesadeayuda.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResguardoUpdateDTO {
    private String tipoEquipo;   // ESCRITORIO_BASICO, MOVIL_AVANZADO, etc.
    private Long id;

    private String usuario;
    private String usuarioFinal;
    private String area;
    private String status;
    private String observaciones;
}

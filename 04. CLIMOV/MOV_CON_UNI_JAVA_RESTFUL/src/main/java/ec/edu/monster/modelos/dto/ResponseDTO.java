package ec.edu.monster.modelos.dto;

import org.json.JSONObject;

public class ResponseDTO {
    private final EstadosEnum estado;
    private final String mensaje;

    protected ResponseDTO(JSONObject json) {
        this.estado = EstadosEnum.desdeTexto(texto(json, "estado"));
        this.mensaje = texto(json, "mensaje");
    }

    public EstadosEnum getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    protected static String texto(JSONObject json, String nombre) {
        Object valor = json.opt(nombre);
        return valor instanceof String ? (String) valor : null;
    }
}

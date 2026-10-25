package ec.edu.monster.modelos.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class ConverterResponseDTO extends ResponseDTO {
    private final Double resultado;

    private ConverterResponseDTO(JSONObject json) {
        super(json);
        Object valor = json.opt("resultado");
        this.resultado = valor instanceof Number ? ((Number) valor).doubleValue() : null;
    }

    public Double getResultado() {
        return resultado;
    }

    public static ConverterResponseDTO desdeJson(String texto) throws JSONException {
        return new ConverterResponseDTO(new JSONObject(texto));
    }
}

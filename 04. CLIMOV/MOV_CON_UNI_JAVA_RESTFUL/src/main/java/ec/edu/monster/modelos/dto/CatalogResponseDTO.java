package ec.edu.monster.modelos.dto;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CatalogResponseDTO extends ResponseDTO {
    private final List<String> resultado;

    private CatalogResponseDTO(JSONObject json) {
        super(json);
        JSONArray arreglo = json.optJSONArray("resultado");
        if (arreglo == null) {
            resultado = null;
            return;
        }
        resultado = new ArrayList<>();
        for (int i = 0; i < arreglo.length(); i++) {
            Object unidad = arreglo.opt(i);
            if (unidad instanceof String) {
                resultado.add((String) unidad);
            }
        }
    }

    public List<String> getResultado() {
        return resultado;
    }

    public static CatalogResponseDTO desdeJson(String texto) throws JSONException {
        return new CatalogResponseDTO(new JSONObject(texto));
    }
}

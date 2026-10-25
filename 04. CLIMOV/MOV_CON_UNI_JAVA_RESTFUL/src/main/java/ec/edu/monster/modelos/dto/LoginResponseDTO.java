package ec.edu.monster.modelos.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginResponseDTO extends ResponseDTO {
    private final String token;

    private LoginResponseDTO(JSONObject json) {
        super(json);
        this.token = texto(json, "token");
    }

    public String getToken() {
        return token;
    }

    public static LoginResponseDTO desdeJson(String texto) throws JSONException {
        return new LoginResponseDTO(new JSONObject(texto));
    }
}

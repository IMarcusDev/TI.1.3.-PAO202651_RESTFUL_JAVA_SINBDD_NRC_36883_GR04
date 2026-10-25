package ec.edu.monster.modelos.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginRequestDTO {
    private final String username;
    private final String password;

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String aJson() throws JSONException {
        return new JSONObject()
                .put("username", username)
                .put("password", password)
                .toString();
    }
}

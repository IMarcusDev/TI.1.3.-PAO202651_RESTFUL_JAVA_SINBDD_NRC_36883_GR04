package ec.edu.monster.modelos.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class ConvertRequestDTO {
    private final String type;
    private final double value;
    private final String from;
    private final String to;

    public ConvertRequestDTO(String type, double value, String from, String to) {
        this.type = type;
        this.value = value;
        this.from = from;
        this.to = to;
    }

    public String aJson() throws JSONException {
        return new JSONObject()
                .put("type", type)
                .put("value", value)
                .put("from", from)
                .put("to", to)
                .toString();
    }
}

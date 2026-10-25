package ec.edu.monster.pruebas.apoyo;

import ec.edu.monster.servicios.ClienteRest;
import ec.edu.monster.servicios.RespuestaHttp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClienteRestFalso extends ClienteRest {

    public interface Respuesta {
        RespuestaHttp para(String ruta) throws IOException;
    }

    public static class Peticion {
        public final String metodo;
        public final String ruta;
        public final String json;
        public final String token;

        Peticion(String metodo, String ruta, String json, String token) {
            this.metodo = metodo;
            this.ruta = ruta;
            this.json = json;
            this.token = token;
        }
    }

    private final Respuesta respuesta;
    private final List<Peticion> peticiones = Collections.synchronizedList(new ArrayList<>());

    public ClienteRestFalso(Respuesta respuesta) {
        super("http://servidor:8080/WS_CON_UNI_JAVA_RESTFUL/api/");
        this.respuesta = respuesta;
    }

    public static ClienteRestFalso responde(int codigo, String cuerpo) {
        return new ClienteRestFalso(ruta -> new RespuestaHttp(codigo, razon(codigo), cuerpo));
    }

    public static ClienteRestFalso lanza(IOException error) {
        return new ClienteRestFalso(ruta -> {
            throw error;
        });
    }

    @Override
    public RespuestaHttp get(String ruta, String token) throws IOException {
        peticiones.add(new Peticion("GET", ruta, null, token));
        return respuesta.para(ruta);
    }

    @Override
    public RespuestaHttp postJson(String ruta, String json, String token) throws IOException {
        peticiones.add(new Peticion("POST", ruta, json, token));
        return respuesta.para(ruta);
    }

    public int cantidad() {
        return peticiones.size();
    }

    public Peticion ultima() {
        return peticiones.get(peticiones.size() - 1);
    }

    private static String razon(int codigo) {
        switch (codigo) {
            case 200: return "OK";
            case 401: return "Unauthorized";
            case 404: return "Not Found";
            case 500: return "Internal Server Error";
            default: return null;
        }
    }

    public static final String HTML_PAYARA = "<!DOCTYPE html><html><head><title>Payara Server - Error report</title>"
            + "</head><body><h1>HTTP Status 404 - Not Found</h1></body></html>";
}

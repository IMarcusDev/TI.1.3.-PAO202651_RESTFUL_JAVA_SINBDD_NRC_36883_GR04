package ec.edu.monster.servicios;

import ec.edu.monster.config.RestConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClienteRest {

    private final String urlBase;
    private final int tiempoEsperaMs;

    public ClienteRest(String urlBase) {
        this(urlBase, RestConfig.TIEMPO_ESPERA_MS);
    }

    public ClienteRest(String urlBase, int tiempoEsperaMs) {
        this.urlBase = urlBase.endsWith("/") ? urlBase : urlBase + "/";
        this.tiempoEsperaMs = tiempoEsperaMs;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public RespuestaHttp get(String ruta, String token) throws IOException {
        return enviar("GET", ruta, null, token);
    }

    public RespuestaHttp postJson(String ruta, String json, String token) throws IOException {
        return enviar("POST", ruta, json, token);
    }

    private RespuestaHttp enviar(String metodo, String ruta, String json, String token) throws IOException {
        HttpURLConnection conexion = null;
        try {
            conexion = (HttpURLConnection) new URL(urlBase + ruta).openConnection();
            conexion.setRequestMethod(metodo);
            conexion.setConnectTimeout(tiempoEsperaMs);
            conexion.setReadTimeout(tiempoEsperaMs);
            conexion.setUseCaches(false);
            conexion.setRequestProperty("Accept", "application/json");
            if (token != null) {
                conexion.setRequestProperty("Authorization", token);
            }

            if (json != null) {
                conexion.setDoOutput(true);
                conexion.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                try (OutputStream salida = conexion.getOutputStream()) {
                    salida.write(json.getBytes(StandardCharsets.UTF_8));
                }
            }

            int codigo = conexion.getResponseCode();
            InputStream flujo = codigo >= 400 ? conexion.getErrorStream() : conexion.getInputStream();
            String cuerpo = flujo != null ? leer(flujo) : "";
            return new RespuestaHttp(codigo, conexion.getResponseMessage(), cuerpo);
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    private static String leer(InputStream flujo) throws IOException {
        try (InputStream entrada = flujo; ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] bloque = new byte[4096];
            int leidos;
            while ((leidos = entrada.read(bloque)) != -1) {
                buffer.write(bloque, 0, leidos);
            }
            return buffer.toString(StandardCharsets.UTF_8.name());
        }
    }
}

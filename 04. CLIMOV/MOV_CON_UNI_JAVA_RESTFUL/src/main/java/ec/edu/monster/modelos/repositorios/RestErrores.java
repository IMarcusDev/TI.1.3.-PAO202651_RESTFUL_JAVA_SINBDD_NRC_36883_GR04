package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.BuildConfig;
import ec.edu.monster.config.RestConfig;
import ec.edu.monster.modelos.dto.ResponseDTO;
import ec.edu.monster.servicios.RespuestaHttp;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public final class RestErrores {

    public static final String SIN_CONEXION = "Sin conexión a internet. Revise el Wi-Fi o los datos móviles.";
    public static final String RESPUESTA_INCOMPLETA = "El servidor envió una respuesta vacía o incompleta.";
    public static final String SESION_EXPIRADA = "Su sesión expiró o no es válida. Inicie sesión nuevamente.";
    public static final String RESULTADO_NO_VALIDO = "El servidor devolvió un resultado no válido.";

    public interface LectorDto<T extends ResponseDTO> {
        T leer(String json) throws JSONException;
    }

    private RestErrores() { }

    public static String mensaje(Throwable ex) {
        String mensaje = mensajeAmigable(ex);
        if (BuildConfig.DEBUG) {
            Throwable causa = causaRaiz(ex);
            mensaje += "\n[Debug] " + causa.getClass().getSimpleName() + ": " + causa.getMessage();
        }
        return mensaje;
    }

    private static String mensajeAmigable(Throwable ex) {
        if (ex instanceof ConnectException || ex instanceof UnknownHostException
                || ex instanceof NoRouteToHostException) {
            return "No se encontró el servidor en " + RestConfig.URL_BASE
                    + ". Verifique que esté encendido y que la IP sea correcta.";
        }
        if (ex instanceof SocketTimeoutException) {
            return "El servidor no respondió en " + RestConfig.TIEMPO_ESPERA_SEGUNDOS + " segundos. Intente de nuevo.";
        }
        if (ex instanceof JSONException) {
            return RESPUESTA_INCOMPLETA;
        }
        if (ex instanceof MalformedURLException) {
            return "La dirección del servidor está mal configurada.";
        }
        if (ex instanceof IOException) {
            return "No se pudo comunicar con el servidor. Verifique que esté encendido y en la misma red.";
        }
        return "Ocurrió un error inesperado al contactar al servidor.";
    }

    private static Throwable causaRaiz(Throwable ex) {
        Throwable causa = ex;
        while (causa.getCause() != null && causa.getCause() != causa) {
            causa = causa.getCause();
        }
        return causa;
    }

    public static <T extends ResponseDTO> T leerDto(RespuestaHttp respuesta, LectorDto<T> lector) {
        if (respuesta.getCuerpo().trim().isEmpty()) {
            return null;
        }
        try {
            T dto = lector.leer(respuesta.getCuerpo());
            return dto.getEstado() == null ? null : dto;
        } catch (JSONException e) {
            return null;
        }
    }

    public static String mensajeSinDto(RespuestaHttp respuesta) {
        return respuesta.esExitosa()
                ? RESPUESTA_INCOMPLETA
                : "El servidor no pudo procesar la solicitud: " + detalleHttp(respuesta);
    }

    private static String detalleHttp(RespuestaHttp respuesta) {
        try {
            Object titulo = new JSONObject(respuesta.getCuerpo()).opt("title");
            if (titulo instanceof String && !((String) titulo).trim().isEmpty()) {
                return ((String) titulo).trim();
            }
        } catch (JSONException e) {
        }
        String razon = respuesta.getRazon() != null ? respuesta.getRazon() : "";
        return ("HTTP " + respuesta.getCodigo() + " " + razon).trim();
    }

    public static String mensajeServidor(String mensaje, String porDefecto) {
        return mensaje == null || mensaje.trim().isEmpty() ? porDefecto : mensaje.trim();
    }

    public static boolean esTokenInvalido(String mensaje) {
        return mensaje != null && mensaje.toLowerCase(Locale.ROOT).contains("token");
    }
}

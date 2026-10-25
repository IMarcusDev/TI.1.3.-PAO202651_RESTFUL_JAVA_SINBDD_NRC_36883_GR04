package ec.edu.monster.pruebas.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.BuildConfig;
import ec.edu.monster.modelos.dto.LoginResponseDTO;
import ec.edu.monster.modelos.repositorios.RestErrores;
import ec.edu.monster.pruebas.apoyo.ClienteRestFalso;
import ec.edu.monster.servicios.RespuestaHttp;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import org.json.JSONException;
import org.junit.Test;

public class RestErroresTest {

    private static final String NO_ENCONTRADO = "No se encontró el servidor en http://192.168.1.2:8080/WS_CON_UNI_JAVA_RESTFUL/api/. "
            + "Verifique que esté encendido y que la IP sea correcta.";
    private static final String SIN_COMUNICACION = "No se pudo comunicar con el servidor. Verifique que esté encendido y en la misma red.";

    @Test
    public void mensaje_cadaExcepcion_tieneSuTextoEnEspanol() {
        Object[][] casos = {
            {new ConnectException("Connection refused"), NO_ENCONTRADO},
            {new UnknownHostException("servidor"), NO_ENCONTRADO},
            {new NoRouteToHostException("sin ruta"), NO_ENCONTRADO},
            {new SocketTimeoutException("timeout"), "El servidor no respondió en 15 segundos. Intente de nuevo."},
            {new JSONException("Unterminated string"), RestErrores.RESPUESTA_INCOMPLETA},
            {new MalformedURLException("no protocol"), "La dirección del servidor está mal configurada."},
            {new UnknownServiceException("CLEARTEXT communication not permitted"), SIN_COMUNICACION},
            {new IOException("reset"), SIN_COMUNICACION},
            {new IllegalStateException("x"), "Ocurrió un error inesperado al contactar al servidor."},
        };
        for (Object[] caso : casos) {
            Throwable error = (Throwable) caso[0];
            String mensaje = RestErrores.mensaje(error);
            assertTrue(error.getClass().getSimpleName() + " → " + mensaje, mensaje.startsWith((String) caso[1]));
        }
    }

    @Test
    public void mensaje_enDebug_agregaLaCausaMasInterna() {
        String mensaje = RestErrores.mensaje(new IOException("envoltura", new ConnectException("refused")));

        if (BuildConfig.DEBUG) {
            assertTrue(mensaje, mensaje.endsWith("\n[Debug] ConnectException: refused"));
        } else {
            assertFalse(mensaje, mensaje.contains("[Debug]"));
        }
    }

    @Test
    public void leerDto_cuerpoVacioHtmlOSinEstado_esNull() {
        for (String cuerpo : new String[] {"", "   ", ClienteRestFalso.HTML_PAYARA, "[]", "{\"token\":\"t\"}"}) {
            assertNull(cuerpo, RestErrores.leerDto(new RespuestaHttp(200, "OK", cuerpo), LoginResponseDTO::desdeJson));
        }
    }

    @Test
    public void leerDto_401ConElDtoDelServidor_seLeeIgual() {
        LoginResponseDTO dto = RestErrores.leerDto(new RespuestaHttp(401, "Unauthorized",
                "{\"estado\":\"ERROR\",\"mensaje\":\"Credenciales Incorrectas\",\"token\":\"\"}"), LoginResponseDTO::desdeJson);

        assertEquals("Credenciales Incorrectas", dto.getMensaje());
    }

    @Test
    public void mensajeSinDto_segunElCodigoHttp() {
        assertEquals(RestErrores.RESPUESTA_INCOMPLETA,
                RestErrores.mensajeSinDto(new RespuestaHttp(200, "OK", "")));
        assertEquals("El servidor no pudo procesar la solicitud: HTTP 404 Not Found",
                RestErrores.mensajeSinDto(new RespuestaHttp(404, "Not Found", ClienteRestFalso.HTML_PAYARA)));
        assertEquals("El servidor no pudo procesar la solicitud: HTTP 500",
                RestErrores.mensajeSinDto(new RespuestaHttp(500, null, "")));
        assertEquals("El servidor no pudo procesar la solicitud: Unsupported Media Type",
                RestErrores.mensajeSinDto(new RespuestaHttp(415, "x", "{\"title\":\" Unsupported Media Type \"}")));
    }

    @Test
    public void esTokenInvalido_soloElMensajeDeToken() {
        assertTrue(RestErrores.esTokenInvalido("Token Inválido"));
        assertFalse(RestErrores.esTokenInvalido("Credenciales Incorrectas"));
        assertFalse(RestErrores.esTokenInvalido("Tipo de Catálogo Inexistente"));
        assertFalse(RestErrores.esTokenInvalido("No se pudo hacer la conversión"));
        assertFalse(RestErrores.esTokenInvalido(null));
    }

    @Test
    public void mensajeServidor_vacio_usaElMensajePorDefecto() {
        assertEquals("Por defecto", RestErrores.mensajeServidor("  ", "Por defecto"));
        assertEquals("Por defecto", RestErrores.mensajeServidor(null, "Por defecto"));
        assertEquals("Credenciales Incorrectas", RestErrores.mensajeServidor(" Credenciales Incorrectas ", "x"));
    }
}

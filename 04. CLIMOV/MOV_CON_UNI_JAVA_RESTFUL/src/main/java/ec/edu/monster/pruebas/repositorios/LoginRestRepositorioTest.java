package ec.edu.monster.pruebas.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.data.LoginResultado;
import ec.edu.monster.modelos.repositorios.LoginRestRepositorio;
import ec.edu.monster.modelos.repositorios.RestErrores;
import ec.edu.monster.pruebas.apoyo.ClienteRestFalso;
import java.net.ConnectException;
import org.json.JSONObject;
import org.junit.Test;

public class LoginRestRepositorioTest {

    private static LoginRestRepositorio repositorio(ClienteRestFalso cliente) {
        return new LoginRestRepositorio(cliente, () -> false);
    }

    @Test
    public void iniciarSesion_ok_devuelveElTokenYEnviaLasCredenciales() throws Exception {
        ClienteRestFalso cliente = ClienteRestFalso.responde(200,
                "{\"estado\":\"OK\",\"mensaje\":\"Ingreso de Sesión Exitoso!\",\"token\":\"abc-123\"}");

        LoginResultado resultado = repositorio(cliente).iniciarSesion("user", "1234");

        assertTrue(resultado.isExito());
        assertEquals("abc-123", resultado.getToken());
        assertEquals("POST", cliente.ultima().metodo);
        assertEquals("login", cliente.ultima().ruta);
        assertNull(cliente.ultima().token);
        JSONObject cuerpo = new JSONObject(cliente.ultima().json);
        assertEquals("user", cuerpo.getString("username"));
        assertEquals("1234", cuerpo.getString("password"));
    }

    @Test
    public void iniciarSesion_credencialesIncorrectas401_muestraElMensajeDelServidor() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(401,
                "{\"estado\":\"ERROR\",\"mensaje\":\"Credenciales Incorrectas\",\"token\":\"\"}");

        LoginResultado resultado = repositorio(cliente).iniciarSesion("user", "x");

        assertFalse(resultado.isExito());
        assertEquals("Credenciales Incorrectas", resultado.getMensaje());
    }

    @Test
    public void iniciarSesion_okSinToken_esRespuestaIncompleta() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(200, "{\"estado\":\"OK\",\"mensaje\":\"ok\",\"token\":\"  \"}");

        assertEquals(RestErrores.RESPUESTA_INCOMPLETA, repositorio(cliente).iniciarSesion("user", "1234").getMensaje());
    }

    @Test
    public void iniciarSesion_htmlDePayara_muestraElCodigoHttp() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(404, ClienteRestFalso.HTML_PAYARA);

        assertEquals("El servidor no pudo procesar la solicitud: HTTP 404 Not Found",
                repositorio(cliente).iniciarSesion("user", "1234").getMensaje());
    }

    @Test
    public void iniciarSesion_sinRed_noLlamaAlServidor() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(200, "");
        LoginRestRepositorio repositorio = new LoginRestRepositorio(cliente, () -> true);

        assertEquals(RestErrores.SIN_CONEXION, repositorio.iniciarSesion("user", "1234").getMensaje());
        assertEquals(0, cliente.cantidad());
    }

    @Test
    public void iniciarSesion_conexionRechazada_noLanzaYDevuelveElMensaje() {
        ClienteRestFalso cliente = ClienteRestFalso.lanza(new ConnectException("refused"));

        LoginResultado resultado = repositorio(cliente).iniciarSesion("user", "1234");

        assertFalse(resultado.isExito());
        assertTrue(resultado.getMensaje(), resultado.getMensaje().startsWith("No se encontró el servidor en"));
    }
}

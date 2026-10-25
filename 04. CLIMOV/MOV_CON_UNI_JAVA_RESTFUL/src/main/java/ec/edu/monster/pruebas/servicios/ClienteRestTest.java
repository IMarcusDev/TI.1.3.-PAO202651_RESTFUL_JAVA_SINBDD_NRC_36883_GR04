package ec.edu.monster.pruebas.servicios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import ec.edu.monster.pruebas.apoyo.ServidorHttpLocal;
import ec.edu.monster.servicios.ClienteRest;
import ec.edu.monster.servicios.RespuestaHttp;
import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClienteRestTest {

    private ServidorHttpLocal servidor;

    @Before
    public void preparar() throws IOException {
        servidor = new ServidorHttpLocal()
                .ruta("/api/login", new ServidorHttpLocal.Respuesta(401, "Unauthorized",
                        "{\"estado\":\"ERROR\",\"mensaje\":\"Credenciales Incorrectas\",\"token\":\"\"}", 0))
                .ruta("/api/catalog/Temperatura", new ServidorHttpLocal.Respuesta(200, "OK",
                        "{\"estado\":\"OK\",\"mensaje\":\"Obtención de Catálogo Exitosa\",\"resultado\":[\"°C\",\"°Ré\"]}", 0))
                .ruta("/api/lento", new ServidorHttpLocal.Respuesta(200, "OK", "{}", 3000));
    }

    @After
    public void liberar() throws IOException {
        servidor.close();
    }

    @Test
    public void postJson_sinToken_enviaJsonUtf8YNoEnviaAuthorization() throws IOException {
        RespuestaHttp respuesta = new ClienteRest(servidor.urlBase()).postJson("login", "{\"username\":\"usuário\"}", null);

        assertEquals("POST", servidor.metodo);
        assertNull(servidor.encabezado("Authorization"));
        assertEquals("application/json; charset=utf-8", servidor.encabezado("Content-Type"));
        assertEquals("application/json", servidor.encabezado("Accept"));
        assertEquals("{\"username\":\"usuário\"}", servidor.cuerpo);
        assertEquals(401, respuesta.getCodigo());
        assertFalse(respuesta.esExitosa());
        assertTrue(respuesta.getCuerpo(), respuesta.getCuerpo().contains("Credenciales Incorrectas"));
    }

    @Test
    public void get_conToken_loEnviaSinBearerYLeeElCuerpoEnUtf8() throws IOException {
        String sinBarraFinal = servidor.urlBase().substring(0, servidor.urlBase().length() - 1);
        RespuestaHttp respuesta = new ClienteRest(sinBarraFinal).get("catalog/Temperatura", "abc-123");

        assertEquals("GET", servidor.metodo);
        assertEquals("/api/catalog/Temperatura", servidor.ruta);
        assertEquals("abc-123", servidor.encabezado("Authorization"));
        assertEquals(200, respuesta.getCodigo());
        assertEquals("OK", respuesta.getRazon());
        assertTrue(respuesta.getCuerpo(), respuesta.getCuerpo().contains("[\"°C\",\"°Ré\"]"));
    }

    @Test
    public void get_errorSinCuerpo_devuelveCuerpoVacio() throws IOException {
        RespuestaHttp respuesta = new ClienteRest(servidor.urlBase()).get("noexiste", "t");

        assertEquals(404, respuesta.getCodigo());
        assertEquals("Not Found", respuesta.getRazon());
        assertEquals("", respuesta.getCuerpo());
    }

    @Test
    public void get_servidorLento_lanzaTimeout() throws IOException {
        try {
            new ClienteRest(servidor.urlBase(), 500).get("lento", "t");
            fail("Debía agotar el tiempo de espera");
        } catch (SocketTimeoutException esperado) {
        }
    }

    @Test
    public void get_puertoSinServidor_lanzaConnectException() throws IOException {
        int puertoLibre;
        try (ServerSocket socket = new ServerSocket(0)) {
            puertoLibre = socket.getLocalPort();
        }
        try {
            new ClienteRest("http://127.0.0.1:" + puertoLibre + "/api/").get("login", null);
            fail("Debía fallar la conexión");
        } catch (ConnectException esperado) {
        }
    }
}

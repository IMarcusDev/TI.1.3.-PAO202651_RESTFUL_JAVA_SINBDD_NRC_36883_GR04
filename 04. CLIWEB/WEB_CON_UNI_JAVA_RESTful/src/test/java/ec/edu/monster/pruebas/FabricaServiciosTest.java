package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import ec.edu.monster.controlador.FabricaServicios;
import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.Sesion;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class FabricaServiciosTest {

    private static final String LOGIN_OK = "{\"estado\":\"OK\",\"mensaje\":\"Ingreso\",\"token\":\"tok-1\"}";

    private HttpServer servidor;
    private String rutaConsultada;

    @AfterEach
    void apagarServidor() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    // Disponible cuando el servidor RESTful responde su recurso de estado.
    @Test
    void disponibleWhenServerAnswersHealthResource() throws IOException {
        FabricaServicios fabrica = new FabricaServicios(arrancar(200, "{}"));

        assertTrue(fabrica.disponible());
        assertEquals("/api/jakartaee11", rutaConsultada);
    }

    // Un error del servidor no cuenta como disponible.
    @Test
    void noDisponibleWhenServerFails() throws IOException {
        FabricaServicios fabrica = new FabricaServicios(arrancar(500, "{}"));

        assertFalse(fabrica.disponible());
    }

    // Caso de la cabecera: el servidor estuvo arriba y luego se cae.
    // Debe pasar a no disponible en la siguiente consulta, sin quedarse con el estado anterior.
    @Test
    void noDisponibleAfterServerGoesDown() throws IOException {
        FabricaServicios fabrica = new FabricaServicios(arrancar(200, "{}"));
        assertTrue(fabrica.disponible());

        servidor.stop(0);

        assertFalse(fabrica.disponible());
    }

    // El servicio creado usa la URL base configurada y queda ligado a la sesión del usuario.
    @Test
    void createdServiceTalksToConfiguredServerAndFillsUserSession() throws IOException, ServicioException {
        FabricaServicios fabrica = new FabricaServicios(arrancar(200, LOGIN_OK));
        Sesion sesion = new Sesion();

        ConversorServicio servicio = fabrica.crearServicio(sesion);
        servicio.iniciarSesion("user", "1234");

        assertEquals("/api/login", rutaConsultada);
        assertTrue(sesion.estaActiva());
        assertEquals("tok-1", sesion.getToken());
    }

    private String arrancar(int estado, String cuerpo) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        servidor.createContext("/", intercambio -> {
            rutaConsultada = intercambio.getRequestURI().getPath();
            byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().add("Content-Type", "application/json");
            intercambio.sendResponseHeaders(estado, bytes.length);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.write(bytes);
            }
        });
        servidor.start();
        return "http://localhost:" + servidor.getAddress().getPort() + "/api";
    }
}

package ec.edu.monster.pruebas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sun.net.httpserver.HttpServer;
import ec.edu.monster.controlador.EstadoServlet;
import ec.edu.monster.controlador.FabricaServicios;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** The page header polls GET /estado to show whether the RESTful server is still up. */
class EstadoServletTest {

    private HttpServer servidor;

    @AfterEach
    void apagarServidor() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void reportsServerGoingDownWithoutCaching() throws IOException, ServletException {
        String urlBase = arrancar();

        assertEquals("{\"disponible\":true}", consultar(urlBase));

        servidor.stop(0);

        assertEquals("{\"disponible\":false}", consultar(urlBase));
    }

    private String consultar(String urlBase) throws IOException, ServletException {
        WebFixture web = new WebFixture();
        when(web.context.getAttribute(WebFixture.FACTORY_ATTRIBUTE)).thenReturn(new FabricaServicios(urlBase));
        StringWriter cuerpo = new StringWriter();
        when(web.response.getWriter()).thenReturn(new PrintWriter(cuerpo));

        web.call(new EstadoServlet(), "GET");

        verify(web.response).setContentType("application/json");
        verify(web.response).setCharacterEncoding("UTF-8");
        verify(web.response).setHeader("Cache-Control", "no-store");
        return cuerpo.toString();
    }

    // Only the health resource answers 200, so the servlet must ask that one.
    private String arrancar() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        servidor.createContext("/", intercambio -> {
            boolean salud = "/api/jakartaee11".equals(intercambio.getRequestURI().getPath());
            intercambio.sendResponseHeaders(salud ? 200 : 404, -1);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.flush();
            }
        });
        servidor.start();
        return "http://localhost:" + servidor.getAddress().getPort() + "/api";
    }
}

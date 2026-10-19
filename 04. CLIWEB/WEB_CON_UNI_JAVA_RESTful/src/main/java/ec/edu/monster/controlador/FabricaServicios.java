package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ConversorServicio;
import ec.edu.monster.modelo.ServicioException;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.modelo.http.ConversorRestApi;
import ec.edu.monster.modelo.http.LoginRestApi;
import ec.edu.monster.modelo.http.RESTfulClient;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Application-scoped holder of the HTTP client of the RESTful server. The
 * client is thread-safe, so it is built once and shared; every user gets its
 * own ConversorServicio bound to its own Sesion.
 */
public class FabricaServicios {

    static final String ATRIBUTO = FabricaServicios.class.getName();
    // Health resource of the server: answers 200 without a token.
    private static final String RECURSO_DISPONIBILIDAD = "jakartaee11";
    private static final Duration TIMEOUT = Duration.ofSeconds(2);

    private final URI disponibilidad;
    private final RESTfulClient restful;
    private final HttpClient cliente = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();

    public FabricaServicios() {
        this(RESTfulClient.URL_BASE);
    }

    /** Points the services and the availability check at the given base URL. */
    public FabricaServicios(String urlBase) {
        String base = urlBase.endsWith("/") ? urlBase : urlBase + "/";
        this.restful = new RESTfulClient(base);
        this.disponibilidad = URI.create(base + RECURSO_DISPONIBILIDAD);
    }

    public static FabricaServicios de(ServletContext contexto) {
        return (FabricaServicios) contexto.getAttribute(ATRIBUTO);
    }

    /**
     * Builds a new per-user service bound to the given session. Building it does not
     * contact the server; the exception is kept so callers handle a factory failure.
     */
    public ConversorServicio crearServicio(Sesion sesion) throws ServicioException {
        return new ConversorServicio(new LoginRestApi(restful), new ConversorRestApi(restful), sesion);
    }

    /** True when the server answers its health resource right now. It asks the server on every call. */
    public boolean disponible() {
        HttpRequest peticion = HttpRequest.newBuilder(disponibilidad).timeout(TIMEOUT).GET().build();
        try {
            HttpResponse<Void> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.discarding());
            return respuesta.statusCode() == 200;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}

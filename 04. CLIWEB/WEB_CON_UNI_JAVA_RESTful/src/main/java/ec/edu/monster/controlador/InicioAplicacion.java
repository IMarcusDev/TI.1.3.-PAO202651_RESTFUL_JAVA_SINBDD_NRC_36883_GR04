package ec.edu.monster.controlador;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Registers the shared service factory when the application starts.
 * The server is contacted only when a request needs it.
 */
@WebListener
public class InicioAplicacion implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        evento.getServletContext().setAttribute(FabricaServicios.ATRIBUTO, new FabricaServicios());
    }
}

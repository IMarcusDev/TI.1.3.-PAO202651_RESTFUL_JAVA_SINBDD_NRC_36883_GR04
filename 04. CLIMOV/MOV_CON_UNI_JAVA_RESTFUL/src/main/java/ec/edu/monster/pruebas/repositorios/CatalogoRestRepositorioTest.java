package ec.edu.monster.pruebas.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.repositorios.CatalogoRestRepositorio;
import ec.edu.monster.modelos.repositorios.RestErrores;
import ec.edu.monster.pruebas.apoyo.ClienteRestFalso;
import java.util.Arrays;
import org.junit.Test;

public class CatalogoRestRepositorioTest {

    private static CatalogoRestRepositorio repositorio(ClienteRestFalso cliente) {
        return new CatalogoRestRepositorio(cliente, () -> false);
    }

    @Test
    public void obtenerCatalogo_ok_devuelveLasUnidadesEnElOrdenDelServidor() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(200,
                "{\"estado\":\"OK\",\"mensaje\":\"Obtención de Catálogo Exitosa\",\"resultado\":[\"°C\",\"°Ré\",\"°F\",\"K\",\"°N\"]}");

        CatalogoResultado resultado = repositorio(cliente).obtenerCatalogo("tok", Magnitud.TEMPERATURA);

        assertTrue(resultado.isExito());
        assertEquals(Arrays.asList("°C", "°Ré", "°F", "K", "°N"), resultado.getUnidades());
        assertEquals("GET", cliente.ultima().metodo);
        assertEquals("catalog/Temperatura", cliente.ultima().ruta);
        assertEquals("tok", cliente.ultima().token);
    }

    @Test
    public void obtenerCatalogo_tokenInvalido401_marcaSesionExpirada() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(401,
                "{\"estado\":\"ERROR\",\"mensaje\":\"Token Inválido\",\"resultado\":[]}");

        CatalogoResultado resultado = repositorio(cliente).obtenerCatalogo("vencido", Magnitud.AREA);

        assertFalse(resultado.isExito());
        assertTrue(resultado.isSesionExpirada());
    }

    @Test
    public void obtenerCatalogo_tipoInexistente401_esErrorPeroNoSesion() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(401,
                "{\"estado\":\"ERROR\",\"mensaje\":\"Tipo de Catálogo Inexistente\",\"resultado\":[]}");

        CatalogoResultado resultado = repositorio(cliente).obtenerCatalogo("tok", Magnitud.AREA);

        assertEquals("Tipo de Catálogo Inexistente", resultado.getMensaje());
        assertFalse(resultado.isSesionExpirada());
    }

    @Test
    public void obtenerCatalogo_okSinUnidades_avisaConLaMagnitud() {
        for (String cuerpo : new String[] {
            "{\"estado\":\"OK\",\"mensaje\":\"ok\",\"resultado\":[]}",
            "{\"estado\":\"OK\",\"mensaje\":\"ok\"}"}) {
            assertEquals(cuerpo, "El servidor no devolvió unidades para Área.",
                    repositorio(ClienteRestFalso.responde(200, cuerpo)).obtenerCatalogo("tok", Magnitud.AREA).getMensaje());
        }
    }

    @Test
    public void obtenerCatalogo_unidadesRepetidasOVacias_seDescartan() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(200,
                "{\"estado\":\"OK\",\"resultado\":[\"m\",\" \",\"m\",\"cm\",5]}");

        assertEquals(Arrays.asList("m", "cm"), repositorio(cliente).obtenerCatalogo("tok", Magnitud.LONGITUD).getUnidades());
    }

    @Test
    public void obtenerCatalogo_200ConHtml_esRespuestaIncompleta() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(200, "<!DOCTYPE html><html><body>Error</body>");

        assertEquals(RestErrores.RESPUESTA_INCOMPLETA,
                repositorio(cliente).obtenerCatalogo("tok", Magnitud.AREA).getMensaje());
    }
}

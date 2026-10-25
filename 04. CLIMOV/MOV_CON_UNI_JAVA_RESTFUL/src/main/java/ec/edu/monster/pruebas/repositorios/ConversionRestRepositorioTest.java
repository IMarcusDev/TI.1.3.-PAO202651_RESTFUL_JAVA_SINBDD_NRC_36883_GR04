package ec.edu.monster.pruebas.repositorios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.data.ConversionResultado;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.repositorios.ConversionRestRepositorio;
import ec.edu.monster.modelos.repositorios.RestErrores;
import ec.edu.monster.pruebas.apoyo.ClienteRestFalso;
import java.net.SocketTimeoutException;
import org.json.JSONObject;
import org.junit.Test;

public class ConversionRestRepositorioTest {

    private static ConversionRestRepositorio repositorio(ClienteRestFalso cliente) {
        return new ConversionRestRepositorio(cliente, () -> false);
    }

    private static Conversion area() {
        return new Conversion(Magnitud.AREA, 2500, "cm2", "m2");
    }

    @Test
    public void convertir_ok_devuelveElResultadoYEnviaElCuerpo() throws Exception {
        ClienteRestFalso cliente = ClienteRestFalso.responde(200,
                "{\"estado\":\"OK\",\"mensaje\":\"Conversión Exitosa\",\"resultado\":0.25}");

        ConversionResultado resultado = repositorio(cliente).convertir("tok", area());

        assertTrue(resultado.isExito());
        assertEquals(0.25, resultado.getValor(), 1e-12);
        assertEquals("POST", cliente.ultima().metodo);
        assertEquals("converter", cliente.ultima().ruta);
        assertEquals("tok", cliente.ultima().token);
        JSONObject cuerpo = new JSONObject(cliente.ultima().json);
        assertEquals("Area", cuerpo.getString("type"));
        assertEquals(2500, cuerpo.getDouble("value"), 0);
        assertEquals("cm2", cuerpo.getString("from"));
        assertEquals("m2", cuerpo.getString("to"));
    }

    @Test
    public void convertir_unidadesUnicode_viajanSinCambios() throws Exception {
        ClienteRestFalso cliente = ClienteRestFalso.responde(200, "{\"estado\":\"OK\",\"resultado\":80.0}");

        repositorio(cliente).convertir("tok", new Conversion(Magnitud.TEMPERATURA, 100, "°C", "°Ré"));

        JSONObject cuerpo = new JSONObject(cliente.ultima().json);
        assertEquals("°C", cuerpo.getString("from"));
        assertEquals("°Ré", cuerpo.getString("to"));
    }

    @Test
    public void convertir_resultadoEnNotacionCientifica_seInterpreta() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(200,
                "{\"estado\":\"OK\",\"mensaje\":\"Conversión Exitosa\",\"resultado\":6.047999999999994E17}");

        assertEquals(6.047999999999994E17, repositorio(cliente).convertir("tok", area()).getValor(), 1);
    }

    @Test
    public void convertir_tokenInvalido401_marcaSesionExpirada() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(401,
                "{\"estado\":\"ERROR\",\"mensaje\":\"Token Inválido\",\"resultado\":0.0}");

        ConversionResultado resultado = repositorio(cliente).convertir("vencido", area());

        assertFalse(resultado.isExito());
        assertTrue(resultado.isSesionExpirada());
    }

    @Test
    public void convertir_conversionFallida401_noEsSesionExpirada() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(401,
                "{\"estado\":\"ERROR\",\"mensaje\":\"No se pudo hacer la conversión\",\"resultado\":0.0}");

        ConversionResultado resultado = repositorio(cliente).convertir("tok", area());

        assertEquals("No se pudo hacer la conversión", resultado.getMensaje());
        assertFalse(resultado.isSesionExpirada());
    }

    @Test
    public void convertir_okSinResultadoOResultadoTexto_esRespuestaIncompleta() {
        for (String cuerpo : new String[] {
            "{\"estado\":\"OK\",\"mensaje\":\"ok\"}",
            "{\"estado\":\"OK\",\"resultado\":\"80\"}"}) {
            assertEquals(cuerpo, RestErrores.RESPUESTA_INCOMPLETA,
                    repositorio(ClienteRestFalso.responde(200, cuerpo)).convertir("tok", area()).getMensaje());
        }
    }

    @Test
    public void convertir_error500DePayara_muestraElCodigoHttp() {
        ClienteRestFalso cliente = ClienteRestFalso.responde(500, ClienteRestFalso.HTML_PAYARA);

        ConversionResultado resultado = repositorio(cliente).convertir("tok", area());

        assertEquals("El servidor no pudo procesar la solicitud: HTTP 500 Internal Server Error", resultado.getMensaje());
        assertFalse(resultado.isSesionExpirada());
    }

    @Test
    public void convertir_timeout_avisaLos15Segundos() {
        ClienteRestFalso cliente = ClienteRestFalso.lanza(new SocketTimeoutException("Read timed out"));

        assertTrue(repositorio(cliente).convertir("tok", area()).getMensaje()
                .startsWith("El servidor no respondió en 15 segundos. Intente de nuevo."));
    }
}

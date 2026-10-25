package ec.edu.monster.pruebas.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import ec.edu.monster.modelos.dto.CatalogResponseDTO;
import ec.edu.monster.modelos.dto.ConvertRequestDTO;
import ec.edu.monster.modelos.dto.ConverterResponseDTO;
import ec.edu.monster.modelos.dto.EstadosEnum;
import ec.edu.monster.modelos.dto.LoginRequestDTO;
import ec.edu.monster.modelos.dto.LoginResponseDTO;
import java.util.Arrays;
import org.json.JSONObject;
import org.junit.Test;

public class DtoJsonTest {

    @Test
    public void loginRequest_usaLosNombresDelServidor() throws Exception {
        JSONObject json = new JSONObject(new LoginRequestDTO("user", "1234").aJson());

        assertEquals("user", json.getString("username"));
        assertEquals("1234", json.getString("password"));
        assertEquals(2, json.length());
    }

    @Test
    public void convertRequest_valorNumericoYUnidadesUnicode() throws Exception {
        JSONObject json = new JSONObject(new ConvertRequestDTO("Temperatura", -12.5, "°C", "°Ré").aJson());

        assertEquals("Temperatura", json.getString("type"));
        assertEquals(-12.5, json.getDouble("value"), 0);
        assertEquals("°C", json.getString("from"));
        assertEquals("°Ré", json.getString("to"));
    }

    @Test
    public void loginResponse_respuestaReal() throws Exception {
        LoginResponseDTO dto = LoginResponseDTO.desdeJson(
                "{\"estado\":\"OK\",\"mensaje\":\"Ingreso de Sesión Exitoso!\",\"token\":\"b39bfa7a-7d47\"}");

        assertEquals(EstadosEnum.OK, dto.getEstado());
        assertEquals("Ingreso de Sesión Exitoso!", dto.getMensaje());
        assertEquals("b39bfa7a-7d47", dto.getToken());
    }

    @Test
    public void respuesta_estadoAusenteODesconocido_quedaNull() throws Exception {
        assertNull(LoginResponseDTO.desdeJson("{\"token\":\"t\"}").getEstado());
        assertNull(LoginResponseDTO.desdeJson("{\"estado\":\"ok\"}").getEstado());
        assertNull(LoginResponseDTO.desdeJson("{\"estado\":0}").getEstado());
        assertEquals(EstadosEnum.ERROR, LoginResponseDTO.desdeJson("{\"estado\":\"ERROR\"}").getEstado());
    }

    @Test
    public void textoNullEnJson_noSeConvierteEnLaPalabraNull() throws Exception {
        assertNull(LoginResponseDTO.desdeJson("{\"estado\":\"OK\",\"token\":null,\"mensaje\":null}").getToken());
    }

    @Test
    public void catalogResponse_conservaElOrdenDelServidor() throws Exception {
        CatalogResponseDTO dto = CatalogResponseDTO.desdeJson(
                "{\"estado\":\"OK\",\"mensaje\":\"x\",\"resultado\":[\"km2\",\"ac\",\"m2\",\"ft2\",\"cm2\",\"ha\"]}");

        assertEquals(Arrays.asList("km2", "ac", "m2", "ft2", "cm2", "ha"), dto.getResultado());
    }

    @Test
    public void catalogResponse_sinListaONoEsLista_esNull() throws Exception {
        assertNull(CatalogResponseDTO.desdeJson("{\"estado\":\"OK\"}").getResultado());
        assertNull(CatalogResponseDTO.desdeJson("{\"estado\":\"OK\",\"resultado\":\"m\"}").getResultado());
    }

    @Test
    public void converterResponse_numeroDecimalYNotacionCientifica() throws Exception {
        assertEquals(247.10516301527602, ConverterResponseDTO.desdeJson(
                "{\"estado\":\"OK\",\"resultado\":247.10516301527602}").getResultado(), 0);
        assertEquals(6.047999999999994E17, ConverterResponseDTO.desdeJson(
                "{\"estado\":\"OK\",\"resultado\":6.047999999999994E17}").getResultado(), 0);
        assertEquals(-40, ConverterResponseDTO.desdeJson(
                "{\"estado\":\"OK\",\"resultado\":-40}").getResultado(), 0);
    }

    @Test
    public void converterResponse_resultadoAusenteOTexto_esNull() throws Exception {
        assertNull(ConverterResponseDTO.desdeJson("{\"estado\":\"OK\"}").getResultado());
        assertNull(ConverterResponseDTO.desdeJson("{\"estado\":\"OK\",\"resultado\":\"80\"}").getResultado());
        assertNull(ConverterResponseDTO.desdeJson("{\"estado\":\"OK\",\"resultado\":null}").getResultado());
    }
}

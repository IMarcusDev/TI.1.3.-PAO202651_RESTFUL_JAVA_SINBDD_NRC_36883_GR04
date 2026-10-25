package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.modelos.data.CatalogoResultado;
import ec.edu.monster.modelos.dto.CatalogResponseDTO;
import ec.edu.monster.modelos.dto.EstadosEnum;
import ec.edu.monster.modelos.enums.Magnitud;
import ec.edu.monster.modelos.interfaces.CatalogoRepositorio;
import ec.edu.monster.modelos.interfaces.VerificadorRed;
import ec.edu.monster.servicios.ClienteRest;
import ec.edu.monster.servicios.RespuestaHttp;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class CatalogoRestRepositorio implements CatalogoRepositorio {

    private final ClienteRest cliente;
    private final VerificadorRed red;

    public CatalogoRestRepositorio(ClienteRest cliente, VerificadorRed red) {
        this.cliente = cliente;
        this.red = red;
    }

    @Override
    public CatalogoResultado obtenerCatalogo(String token, Magnitud magnitud) {
        if (red.sinRed()) {
            return CatalogoResultado.error(RestErrores.SIN_CONEXION);
        }

        try {
            RespuestaHttp respuesta = cliente.get(
                    "catalog/" + URLEncoder.encode(magnitud.getCodigoServidor(), "UTF-8"), token);

            CatalogResponseDTO dto = RestErrores.leerDto(respuesta, CatalogResponseDTO::desdeJson);
            if (dto == null) {
                return CatalogoResultado.error(RestErrores.mensajeSinDto(respuesta));
            }

            if (dto.getEstado() != EstadosEnum.OK) {
                return new CatalogoResultado(false,
                        RestErrores.mensajeServidor(dto.getMensaje(), "No se pudo obtener el catálogo"),
                        Collections.emptyList(),
                        RestErrores.esTokenInvalido(dto.getMensaje()));
            }

            Set<String> unicas = new LinkedHashSet<>();
            if (dto.getResultado() != null) {
                for (String unidad : dto.getResultado()) {
                    if (!unidad.trim().isEmpty()) {
                        unicas.add(unidad.trim());
                    }
                }
            }

            if (unicas.isEmpty()) {
                return CatalogoResultado.error("El servidor no devolvió unidades para " + magnitud.getNombre() + ".");
            }

            return CatalogoResultado.ok(dto.getMensaje() != null ? dto.getMensaje() : "", new ArrayList<>(unicas));
        } catch (Exception e) {
            return CatalogoResultado.error(RestErrores.mensaje(e));
        }
    }
}

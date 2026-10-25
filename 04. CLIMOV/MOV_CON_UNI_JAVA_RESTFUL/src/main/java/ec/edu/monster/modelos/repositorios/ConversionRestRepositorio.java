package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.modelos.data.Conversion;
import ec.edu.monster.modelos.data.ConversionResultado;
import ec.edu.monster.modelos.dto.ConvertRequestDTO;
import ec.edu.monster.modelos.dto.ConverterResponseDTO;
import ec.edu.monster.modelos.dto.EstadosEnum;
import ec.edu.monster.modelos.interfaces.ConversionRepositorio;
import ec.edu.monster.modelos.interfaces.VerificadorRed;
import ec.edu.monster.servicios.ClienteRest;
import ec.edu.monster.servicios.RespuestaHttp;

public class ConversionRestRepositorio implements ConversionRepositorio {

    private final ClienteRest cliente;
    private final VerificadorRed red;

    public ConversionRestRepositorio(ClienteRest cliente, VerificadorRed red) {
        this.cliente = cliente;
        this.red = red;
    }

    @Override
    public ConversionResultado convertir(String token, Conversion conversion) {
        if (red.sinRed()) {
            return ConversionResultado.error(RestErrores.SIN_CONEXION);
        }

        try {
            String json = new ConvertRequestDTO(
                    conversion.getMagnitud().getCodigoServidor(),
                    conversion.getValor(),
                    conversion.getUnidadOrigen(),
                    conversion.getUnidadDestino()).aJson();

            RespuestaHttp respuesta = cliente.postJson("converter", json, token);

            ConverterResponseDTO dto = RestErrores.leerDto(respuesta, ConverterResponseDTO::desdeJson);
            if (dto == null) {
                return ConversionResultado.error(RestErrores.mensajeSinDto(respuesta));
            }

            if (dto.getEstado() != EstadosEnum.OK) {
                return new ConversionResultado(false,
                        RestErrores.mensajeServidor(dto.getMensaje(), "No se pudo hacer la conversión"),
                        0,
                        RestErrores.esTokenInvalido(dto.getMensaje()),
                        null);
            }

            Double resultado = dto.getResultado();
            if (resultado == null) {
                return ConversionResultado.error(RestErrores.RESPUESTA_INCOMPLETA);
            }
            if (Double.isNaN(resultado) || Double.isInfinite(resultado)) {
                return ConversionResultado.error(RestErrores.RESULTADO_NO_VALIDO);
            }

            return ConversionResultado.ok(dto.getMensaje() != null ? dto.getMensaje() : "", resultado);
        } catch (Exception e) {
            return ConversionResultado.error(RestErrores.mensaje(e));
        }
    }
}

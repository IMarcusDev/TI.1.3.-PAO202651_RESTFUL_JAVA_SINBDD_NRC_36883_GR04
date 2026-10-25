package ec.edu.monster.modelos.repositorios;

import ec.edu.monster.modelos.data.LoginResultado;
import ec.edu.monster.modelos.dto.EstadosEnum;
import ec.edu.monster.modelos.dto.LoginRequestDTO;
import ec.edu.monster.modelos.dto.LoginResponseDTO;
import ec.edu.monster.modelos.interfaces.LoginRepositorio;
import ec.edu.monster.modelos.interfaces.VerificadorRed;
import ec.edu.monster.servicios.ClienteRest;
import ec.edu.monster.servicios.RespuestaHttp;

public class LoginRestRepositorio implements LoginRepositorio {

    private final ClienteRest cliente;
    private final VerificadorRed red;

    public LoginRestRepositorio(ClienteRest cliente, VerificadorRed red) {
        this.cliente = cliente;
        this.red = red;
    }

    @Override
    public LoginResultado iniciarSesion(String usuario, String clave) {
        if (red.sinRed()) {
            return LoginResultado.error(RestErrores.SIN_CONEXION);
        }

        try {
            String json = new LoginRequestDTO(usuario, clave).aJson();
            RespuestaHttp respuesta = cliente.postJson("login", json, null);

            LoginResponseDTO dto = RestErrores.leerDto(respuesta, LoginResponseDTO::desdeJson);
            if (dto == null) {
                return LoginResultado.error(RestErrores.mensajeSinDto(respuesta));
            }

            if (dto.getEstado() != EstadosEnum.OK) {
                return LoginResultado.error(
                        RestErrores.mensajeServidor(dto.getMensaje(), "Usuario o contraseña incorrectos"));
            }

            String token = dto.getToken();
            if (token == null || token.trim().isEmpty()) {
                return LoginResultado.error(RestErrores.RESPUESTA_INCOMPLETA);
            }

            return new LoginResultado(true, dto.getMensaje() != null ? dto.getMensaje() : "", token);
        } catch (Exception e) {
            return LoginResultado.error(RestErrores.mensaje(e));
        }
    }
}

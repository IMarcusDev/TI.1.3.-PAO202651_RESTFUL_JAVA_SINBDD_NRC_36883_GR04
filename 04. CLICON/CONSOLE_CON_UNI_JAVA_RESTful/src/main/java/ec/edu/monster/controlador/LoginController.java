/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.modelos.dto.LoginResponseDTO;
import ec.edu.monster.modelos.enums.EndpointsEnum;
import ec.edu.monster.modelos.enums.EstadosEnum;
import ec.edu.monster.modelos.http.RESTfulClient;
import ec.edu.monster.modelos.http.ConfigHTTP;
import org.json.JSONObject;

/**
 *
 * @author Mateo Sosa
 */
public class LoginController {
  public LoginResponseDTO login(String username, String password) {
    RESTfulClient client = new RESTfulClient();

    JSONObject body = new JSONObject();
    body.put("username", username);
    body.put("password", password);

    try {
      String json = client.post(EndpointsEnum.login, body.toString());

      LoginResponseDTO dto = LoginResponseDTO.fromJSON(json);
      
      ConfigHTTP.token = dto.getToken();
      
      return dto;
    } catch (Exception e) {
      return new LoginResponseDTO(
        EstadosEnum.ERROR,
        "Error al iniciar sesión: " + e.getMessage(),
        null
      );
    }
  }
}

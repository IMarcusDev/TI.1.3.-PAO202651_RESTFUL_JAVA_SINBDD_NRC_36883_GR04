/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.api;

import ec.edu.monster.controlador.LoginController;
import ec.edu.monster.modelos.dto.LoginResponseDTO;
import ec.edu.monster.modelos.dto.UserDTO;
import ec.edu.monster.modelos.enums.EstadosEnum;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author Mateo Sosa
 */
@Path("login")
public class LoginEndpoint {
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response loginUser(UserDTO login) {
    String username = login.getUsername();
    String password = login.getPassword();
    
    LoginResponseDTO dto;
    try {
      String token = LoginController.login(username, password);
      
     dto = new LoginResponseDTO(
        EstadosEnum.OK,
        "Ingreso de Sesión Exitoso!",
        token
      );
    } catch (Exception e) {
      dto = new LoginResponseDTO(
        EstadosEnum.ERROR,
        "Credenciales Incorrectas",
        ""
      );
    }
    
    return Response
      .status(dto.getEstado() == EstadosEnum.OK ? 200 : 401)
      .entity(dto)
      .type(MediaType.APPLICATION_JSON)
      .build();
  }
}

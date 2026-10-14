package ec.edu.monster.resources;

import ec.edu.monster.modelos.dto.LoginResponseDTO;
import ec.edu.monster.modelos.dto.ResponseDTO;
import ec.edu.monster.modelos.enums.EstadosEnum;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author Mateo Sosa
 */
@Path("jakartaee11")
public class JakartaEE11Resource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response ping() {
    ResponseDTO dto = new ResponseDTO(EstadosEnum.OK, "Payara Server Working!");

    return Response
      .ok(dto)
      .build();
  }
}

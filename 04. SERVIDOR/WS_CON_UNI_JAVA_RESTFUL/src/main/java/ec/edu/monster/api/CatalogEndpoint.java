/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.api;

import ec.edu.monster.controlador.ConverterController;
import ec.edu.monster.controlador.LoginController;
import ec.edu.monster.modelos.dto.CatalogResponseDTO;
import ec.edu.monster.modelos.enums.EstadosEnum;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collections;

/**
 *
 * @author Mateo Sosa
 */
@Path("catalog")
public class CatalogEndpoint {
  private static String formatType(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    
    // Capitalize & lowercase remain
    return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
  }
  
  @GET
  @Path("{type}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getCatalog(
    @PathParam("type") String type,
    @HeaderParam("Authorization") String token    
  ) {
    type = CatalogEndpoint.formatType(type);
    
    CatalogResponseDTO dto;
    if (!LoginController.validateUser(token)) {
      dto = new CatalogResponseDTO(
        EstadosEnum.ERROR,
        "Token Inválido",
        Collections.emptySet()
      );
      
      return Response
        .status(dto.getEstado() == EstadosEnum.OK ? 200 : 401)
        .entity(dto)
        .type(MediaType.APPLICATION_JSON)
        .build();
    }
    
    try {
      dto = new CatalogResponseDTO(
        EstadosEnum.OK,
        "Obtención de Catálogo Exitosa",
        ConverterController.getConverter(type).getUnits()
      );
    } catch (Exception e) {
      dto = new CatalogResponseDTO(
        EstadosEnum.ERROR,
        "Tipo de Catálogo Inexistente",
        Collections.emptySet()
      );
    }
    
    return Response
      .status(dto.getEstado() == EstadosEnum.OK ? 200 : 401)
      .entity(dto)
      .type(MediaType.APPLICATION_JSON)
      .build();
  }
  
}

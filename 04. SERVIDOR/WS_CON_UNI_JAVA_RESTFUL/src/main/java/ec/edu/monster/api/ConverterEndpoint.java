/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.api;

import ec.edu.monster.controlador.ConverterController;
import ec.edu.monster.controlador.LoginController;
import ec.edu.monster.modelos.dto.ConvertDTO;
import ec.edu.monster.modelos.dto.ConverterResponseDTO;
import ec.edu.monster.modelos.enums.EstadosEnum;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author Mateo Sosa
 */
@Path("converter")
public class ConverterEndpoint {
  
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response convert(
    @HeaderParam("Authorization") String token,
    ConvertDTO convert
  ) {
    String type = convert.getType();
    double value = convert.getValue();
    String from = convert.getFrom();
    String to = convert.getTo();
    
    ConverterResponseDTO dto;
    if (!LoginController.validateUser(token)) {
      dto = new ConverterResponseDTO(EstadosEnum.ERROR, "Token Inválido", 0);
      
      return Response
        .status(dto.getEstado() == EstadosEnum.OK ? 200 : 401)
        .entity(dto)
        .type(MediaType.APPLICATION_JSON)
        .build();
    }

    try {
      dto = new ConverterResponseDTO(
        EstadosEnum.OK,
        "Conversión Exitosa",
        ConverterController.convert(type, value, from, to)
      );
    } catch (Exception e) {
      dto = new ConverterResponseDTO(
        EstadosEnum.ERROR,
        "No se pudo hacer la conversión",
        0
      );
    }

    return Response
      .status(dto.getEstado() == EstadosEnum.OK ? 200 : 401)
      .entity(dto)
      .type(MediaType.APPLICATION_JSON)
      .build();
  }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.controlador;

import ec.edu.monster.modelos.dto.CatalogResponseDTO;
import ec.edu.monster.modelos.dto.ConversorResponseDTO;
import ec.edu.monster.modelos.enums.EndpointsEnum;
import ec.edu.monster.modelos.enums.EstadosEnum;
import ec.edu.monster.modelos.enums.TypesEnum;
import ec.edu.monster.modelos.http.ConfigHTTP;
import ec.edu.monster.modelos.http.RESTfulClient;
import java.util.Collections;
import java.util.HashSet;
import org.json.JSONObject;

/**
 *
 * @author Mateo Sosa
 */
public class ConversorController {
  private final TypesEnum type;
  private final RESTfulClient client;
  
  public ConversorController(TypesEnum type) {
    this.type = type;
    this.client = new RESTfulClient();
  }

  public ConversorResponseDTO makeConversion(double value, String from, String to) {
    JSONObject body = new JSONObject();
    body.put("type", this.type);
    body.put("value", value);
    body.put("from", from);
    body.put("to", to);
    
    try {
      String json = this.client.post(
        EndpointsEnum.converter,
        body.toString(),
        ConfigHTTP.token
      );

      return ConversorResponseDTO.fromJSON(json);
    } catch (Exception e) {
      return new ConversorResponseDTO(
        EstadosEnum.ERROR,
        "Error al realizar conversión: " + e.getMessage(),
        0
      );
    }
  }
  
  public CatalogResponseDTO getCatalog() {
    try {
      String json = this.client.get(
        EndpointsEnum.catalog.toString() + "/" + this.type,
        ConfigHTTP.token
      );

      return CatalogResponseDTO.fromJSON(json);
    } catch (Exception e) {
      return new CatalogResponseDTO(
        EstadosEnum.ERROR,
        "Error al obtener catálogo: " + e.getMessage(),
        new HashSet<String>()
      );
    }
  }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.dto;

import ec.edu.monster.modelos.enums.EstadosEnum;
import java.util.Collections;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mateo Sosa
 */
public class CatalogResponseDTO extends ResponseDTO {
  private HashSet<String> resultado;
  
  public CatalogResponseDTO(EstadosEnum estado, String mensaje, HashSet<String> resultado) {
    super(estado, mensaje);
    
    this.resultado = resultado;
  }

  public HashSet<String> getResultado() {
    return resultado;
  }

  public void setResultado(HashSet<String> resultado) {
    this.resultado = resultado;
  }
  
  public static CatalogResponseDTO fromJSON(String json) {
    ResponseDTO response = ResponseDTO.fromJSON(json);
    JSONObject obj = new JSONObject(json);

    HashSet<String> resultado = new HashSet<>();

    JSONArray array = obj.optJSONArray("resultado");

    if (array != null) {
      for (int i = 0; i < array.length(); i++) {
        resultado.add(array.getString(i));
      }
    }
    
    return new CatalogResponseDTO(
      response.getEstado(),
      response.getMensaje(),
      resultado
    );
  }
}

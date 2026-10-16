/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.dto;

import ec.edu.monster.modelos.enums.EstadosEnum;
import org.json.JSONObject;

/**
 *
 * @author Mateo Sosa
 */
public class ConversorResponseDTO extends ResponseDTO {
  private double resultado;
  
  public ConversorResponseDTO(EstadosEnum estado, String mensaje, double resultado) {
    super(estado, mensaje);
    
    this.resultado = resultado;
  }

  public double getResultado() {
    return resultado;
  }

  public void setResultado(double resultado) {
    this.resultado = resultado;
  }
  
  public static ConversorResponseDTO fromJSON(String json) {
    ResponseDTO response = ResponseDTO.fromJSON(json);
    JSONObject obj = new JSONObject(json);

    double resultado = obj.optDouble("resultado", 0);
    
    return new ConversorResponseDTO(
      response.getEstado(),
      response.getMensaje(),
      resultado
    );
  }
}

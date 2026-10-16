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
public class ResponseDTO {
  public EstadosEnum estado;
  public String mensaje;
  
  public ResponseDTO(EstadosEnum estado, String mensaje) {
    this.estado = estado;
    this.mensaje = mensaje;
  }

  public EstadosEnum getEstado() {
    return estado;
  }

  public void setEstado(EstadosEnum estado) {
    this.estado = estado;
  }

  public String getMensaje() {
    return mensaje;
  }

  public void setMensaje(String mensaje) {
    this.mensaje = mensaje;
  }
  
  public static ResponseDTO fromJSON(String json) {
    JSONObject obj = new JSONObject(json);

    EstadosEnum estado = EstadosEnum.valueOf(obj.getString("estado"));
    String mensaje = obj.getString("mensaje");

    return new ResponseDTO(estado, mensaje);
  }
}

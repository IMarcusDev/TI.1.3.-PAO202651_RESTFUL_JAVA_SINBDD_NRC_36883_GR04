/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.dto;

import ec.edu.monster.modelos.enums.EstadosEnum;

/**
 *
 * @author Mateo Sosa
 */
public class LoginResponseDTO extends ResponseDTO {
  private String token;
  
  public LoginResponseDTO(EstadosEnum estado, String mensaje, String token) {
    super(estado, mensaje);
    
    this.token = token;
  }
  
  public void setToken(String token) {
    this.token = token;
  }
  
  public String getToken() {
    return this.token;
  }
}

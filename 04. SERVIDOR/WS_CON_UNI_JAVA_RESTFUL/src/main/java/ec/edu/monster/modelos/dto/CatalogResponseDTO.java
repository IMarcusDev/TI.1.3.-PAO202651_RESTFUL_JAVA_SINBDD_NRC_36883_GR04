/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.dto;

import ec.edu.monster.modelos.enums.EstadosEnum;
import java.util.Set;

/**
 *
 * @author Mateo Sosa
 */
public class CatalogResponseDTO extends ResponseDTO {
  private Set<String> resultado;
  
  public CatalogResponseDTO(EstadosEnum estado, String mensaje, Set<String> resultado) {
    super(estado, mensaje);
    
    this.resultado = resultado;
  }
 
  public void setResultado(Set<String> resultado) {
    this.resultado = resultado;
  }
  
  public Set<String> getResultado() {
    return this.resultado;
  }
}

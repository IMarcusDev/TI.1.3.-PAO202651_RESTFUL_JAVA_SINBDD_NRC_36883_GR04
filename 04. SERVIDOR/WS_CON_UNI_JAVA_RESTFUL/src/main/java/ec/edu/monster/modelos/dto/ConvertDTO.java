/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.dto;

/**
 *
 * @author Mateo Sosa
 */
public class ConvertDTO {
  private String type;
  private double value;
  private String from;
  private String to;

  public ConvertDTO() {
  }

  public ConvertDTO(String type, double value, String from, String to) {
    this.type = type;
    this.value = value;
    this.from = from;
    this.to = to;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }
  
  
}

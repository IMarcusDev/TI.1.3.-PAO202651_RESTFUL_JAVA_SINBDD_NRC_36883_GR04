/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.modelos.http;

import ec.edu.monster.modelos.enums.EndpointsEnum;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 *
 * @author Mateo Sosa
 */
public class RESTfulClient {
  private String baseUrl = "http://localhost:8080/WS_CON_UNI_JAVA_RESTFUL/api/";
  private HttpClient http;

  public RESTfulClient() {
    this.http = HttpClient.newHttpClient();
  }

  public String get(EndpointsEnum endpoint) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + endpoint))
            .header("Accept", "application/json")
            .GET()
            .build();

    HttpResponse<String> response = http.send(
            request,
            HttpResponse.BodyHandlers.ofString()
    );

    return response.body();
  }

  public String post(EndpointsEnum endpoint, String json, String token) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(baseUrl + endpoint))
      .header("Content-Type", "application/json")
      .header("Authorization", token)
      .header("Accept", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(json))
      .build();

    HttpResponse<String> response = http.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    return response.body();
  }
  
  public String post(EndpointsEnum endpoint, String json) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(baseUrl + endpoint))
      .header("Content-Type", "application/json")
      .header("Accept", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString(json))
      .build();

    HttpResponse<String> response = http.send(
      request,
      HttpResponse.BodyHandlers.ofString()
    );

    return response.body();
  }
}

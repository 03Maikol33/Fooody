package it.maikol.fooodyweb.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

/**
 * Controller per la registrazione di un nuovo cliente.
 * /register
 */
public class RegisterController extends BaseController {

    private static final String API_BASE = "http://localhost:8080/Fooody/api";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        String via = request.getParameter("via");
        String civico = request.getParameter("civico");
        String citta = request.getParameter("citta");
        String password = request.getParameter("password");
        
        if (via == null || via.isBlank()) {
            String indirizzo = request.getParameter("indirizzo");
            via = indirizzo != null ? indirizzo : "Via Non Specificata";
            civico = "SNC";
            citta = "Alba Adriatica";
        }

        HashMap<String, Object> data = new HashMap<>();

        try {
            //crea il json body per la richiesta di registrazione rimuovendo eventuali caratteri speciali che potrebbero interferire con il JSON
            String jsonBody = String.format(
                "{\"nome\":\"%s\", \"cognome\":\"%s\", \"email\":\"%s\", \"telefono\":\"%s\", \"via\":\"%s\", \"civico\":\"%s\", \"citta\":\"%s\", \"password\":\"%s\", \"ruolo\":\"cliente\"}", 
                nome.replace("\"", "\\\""), cognome.replace("\"", "\\\""), email.replace("\"", "\\\""), 
                telefono.replace("\"", "\\\""), via.replace("\"", "\\\""), civico.replace("\"", "\\\""), 
                citta.replace("\"", "\\\""), password.replace("\"", "\\\"")
            );
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
            
            if (apiResponse.statusCode() == 200 || apiResponse.statusCode() == 201) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            } else {
                data.put("regError", "Errore durante la registrazione: " + apiResponse.body());
            }
        } catch (Exception e) {
            data.put("regError", "Errore di sistema: " + e.getMessage());
        }
        
        renderTemplate("login.ftl", data, response);
    }
}
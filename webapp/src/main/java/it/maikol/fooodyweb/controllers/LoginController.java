/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooodyweb.controllers;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.maikol.fooodyweb.models.AuthResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
/**
 *
 * @author Maikol
 */
public class LoginController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, Object> data = new HashMap<>();
        renderTemplate("login.ftl", data, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        HashMap<String, Object> data = new HashMap<>();
        data.put("context", request.getContextPath());
        
        try {
            
            String jsonBody = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}";
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/Fooody/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            
            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
            
            if (apiResponse.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                AuthResponse authRes = mapper.readValue(apiResponse.body(), AuthResponse.class);
                
                //Creazione sessione e salvataggio token, ruolo e utente
                jakarta.servlet.http.HttpSession session = request.getSession(true);
                session.setAttribute("tokenJWT", authRes.getToken());
                session.setAttribute("ruoloUtente", authRes.getRuolo());
                session.setAttribute("utente", authRes.getUtente());
                
                
                // Reindirizzamento in base al ruolo dell'utente
                if ("proprietario".equals(authRes.getRuolo())) {
                    response.sendRedirect(request.getContextPath() + "/owner-orders");
                } else if ("personale".equals(authRes.getRuolo()) || "staff".equals(authRes.getRuolo())) {
                    response.sendRedirect(request.getContextPath() + "/staff-orders");
                } else {
                    response.sendRedirect(request.getContextPath() + "/index");
                }
                return;
                
            } else if (apiResponse.statusCode() == 401) {
                data.put("loginError", "Email o password non validi.");
                data.put("emailInserita", email);
            } else {
                data.put("loginError", "Errore di connessione al server.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            data.put("loginError", "Errore interno del sistema.");
        }

        renderTemplate("login.ftl", data, response);
    }
}

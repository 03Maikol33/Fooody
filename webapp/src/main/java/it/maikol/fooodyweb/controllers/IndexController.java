/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooodyweb.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.maikol.fooodyweb.models.Prodotto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 *
 * @author Maikol
 */
public class IndexController extends BaseController{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, Object> data = new HashMap<>();
        data.put("context", request.getContextPath());
        
        // gestione sessione
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("utente") != null) {
            data.put("utenteLoggato", session.getAttribute("utente"));
        }
        
        List<Prodotto> prodottiAnteprima = new ArrayList<>();
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/Fooody/api/prodotti")) 
                    .GET()
                    .build();
            
            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
            
            if (apiResponse.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                
                prodottiAnteprima = mapper.readValue(apiResponse.body(), new TypeReference<List<Prodotto>>(){});
                String token = (session != null) ? (String) session.getAttribute("tokenJWT") : null;
                popolaIngredienti(prodottiAnteprima, token);
                
                if(prodottiAnteprima.size() > 3) {
                    prodottiAnteprima = prodottiAnteprima.subList(0, 3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //passa la lista al template di FreeMarker
        data.put("prodotti", prodottiAnteprima);
        
        renderTemplate("index.ftl", data, response);
    }
}

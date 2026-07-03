package it.maikol.fooodyweb.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.maikol.fooodyweb.models.Ingrediente;
import it.maikol.fooodyweb.models.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller per la gestione globale degli ingredienti
 */
public class OwnerIngredientsController extends BaseController {

    private static final String API_BASE = "http://localhost:8080/Fooody/api";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        HashMap<String, Object> data = new HashMap<>();

        if (!checkRole(session, response, request, "proprietario")) return;

        Utente utente = (Utente) session.getAttribute("utente");
        String token = (String) session.getAttribute("tokenJWT");
        data.put("utenteLoggato", utente);

        List<Ingrediente> ingredienti = new ArrayList<>();
        String errorMsg = null;

        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/ingredienti"))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();
                HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (res.statusCode() == 200) {
                    ingredienti = mapper.readValue(res.body(), new TypeReference<List<Ingrediente>>() {});
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "Errore di connessione all'API.";
        }

        data.put("ingredienti", ingredienti);
        if (errorMsg != null) data.put("errorMsg", errorMsg);

        String msg = request.getParameter("msg");
        String detail = request.getParameter("detail");
        if ("created".equals(msg)) data.put("successMsg", "Ingrediente creato con successo!");
        else if ("deleted".equals(msg)) data.put("successMsg", "Ingrediente rimosso con successo!");
        else if ("error".equals(msg)) data.put("errorMsg", detail != null ? detail : "Errore durante l'operazione.");

        renderTemplate("owner-ingredients.ftl", data, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (!checkRole(session, response, request, "proprietario")) return;

        String token = (String) session.getAttribute("tokenJWT");
        String action = request.getParameter("action");
        String contextPath = request.getContextPath();

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        try {
            if ("create".equals(action)) {
                String nome = request.getParameter("nome");
                Map<String, Object> body = new HashMap<>();
                body.put("nome", nome != null ? nome.trim() : "");

                String jsonBody = mapper.writeValueAsString(body);
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/ingredienti"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 201 || apiRes.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-ingredients?msg=created");
                } else {
                    response.sendRedirect(contextPath + "/owner-ingredients?msg=error&detail=Errore+creazione+ingrediente");
                }

            } else if ("delete".equals(action)) {
                String idStr = request.getParameter("idIngrediente");
                if (idStr != null) {
                    HttpRequest apiRequest = HttpRequest.newBuilder()
                            .uri(URI.create(API_BASE + "/ingredienti/" + idStr))
                            .header("Authorization", "Bearer " + token)
                            .DELETE()
                            .build();

                    HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                    if (apiRes.statusCode() == 204 || apiRes.statusCode() == 200) {
                        response.sendRedirect(contextPath + "/owner-ingredients?msg=deleted");
                    } else if (apiRes.statusCode() == 409) {
                        response.sendRedirect(contextPath + "/owner-ingredients?msg=error&detail=Ingrediente+in+uso+in+alcune+ricette");
                    } else {
                        response.sendRedirect(contextPath + "/owner-ingredients?msg=error&detail=Errore+durante+la+rimozione");
                    }
                } else {
                    response.sendRedirect(contextPath + "/owner-ingredients");
                }
            } else {
                response.sendRedirect(contextPath + "/owner-ingredients");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/owner-ingredients?msg=error&detail=Errore+di+comunicazione");
        }
    }

    private boolean checkRole(HttpSession session, HttpServletResponse response,
                              HttpServletRequest request, String ruoloRichiesto) throws IOException {
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        Utente u = (Utente) session.getAttribute("utente");
        if (!ruoloRichiesto.equals(u.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}

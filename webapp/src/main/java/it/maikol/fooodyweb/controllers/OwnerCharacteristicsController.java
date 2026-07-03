package it.maikol.fooodyweb.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.maikol.fooodyweb.models.Caratteristica;
import it.maikol.fooodyweb.models.GruppoGmc;
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

public class OwnerCharacteristicsController extends BaseController {

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

        List<GruppoGmc> gruppi = new ArrayList<>();
        List<Caratteristica> caratteristiche = new ArrayList<>();
        String errorMsg = null;

        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //ottenimento gruppi di mutua esclusione
            try {
                HttpRequest gReq = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche/gruppi"))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();
                HttpResponse<String> gRes = client.send(gReq, HttpResponse.BodyHandlers.ofString());
                if (gRes.statusCode() == 200) {
                    gruppi = mapper.readValue(gRes.body(), new TypeReference<List<GruppoGmc>>() {});
                }
            } catch (Exception ex) {}

            //ottenimento caratteristiche
            try {
                HttpRequest cReq = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche"))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();
                HttpResponse<String> cRes = client.send(cReq, HttpResponse.BodyHandlers.ofString());
                if (cRes.statusCode() == 200) {
                    caratteristiche = mapper.readValue(cRes.body(), new TypeReference<List<Caratteristica>>() {});
                }
            } catch (Exception ex) {}

        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "Errore di connessione all'API.";
        }

        data.put("gruppi", gruppi);
        data.put("caratteristiche", caratteristiche);
        if (errorMsg != null) data.put("errorMsg", errorMsg);

        String msg = request.getParameter("msg");
        String detail = request.getParameter("detail");
        if ("created".equals(msg)) data.put("successMsg", "Elemento creato con successo!");
        else if ("updated".equals(msg)) data.put("successMsg", "Elemento modificato con successo!");
        else if ("deleted".equals(msg)) data.put("successMsg", "Elemento rimosso con successo!");
        else if ("error".equals(msg)) data.put("errorMsg", detail != null ? detail : "Errore durante l'operazione.");

        renderTemplate("owner-characteristics.ftl", data, response);
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
            if ("addGmc".equals(action)) {
                Map<String, Object> body = new HashMap<>();
                body.put("nome", request.getParameter("nome"));
                body.put("descrizione", request.getParameter("descrizione") != null ? request.getParameter("descrizione") : "");

                String jsonBody = mapper.writeValueAsString(body);
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche/gruppi"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 201 || apiRes.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=created");
                } else {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=error&detail=Errore+creazione+gruppo");
                }

            } else if ("editGmc".equals(action)) {
                String idGmc = request.getParameter("idGmc");
                Map<String, Object> body = new HashMap<>();
                body.put("nome", request.getParameter("nome"));
                body.put("descrizione", request.getParameter("descrizione") != null ? request.getParameter("descrizione") : "");

                String jsonBody = mapper.writeValueAsString(body);
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche/gruppi/" + idGmc))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 200 || apiRes.statusCode() == 204) {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=updated");
                } else {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=error&detail=Errore+modifica+gruppo");
                }

            } else if ("deleteGmc".equals(action)) {
                String idGmc = request.getParameter("idGmc");
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche/gruppi/" + idGmc))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 200 || apiRes.statusCode() == 204) {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=deleted");
                } else {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=error&detail=Errore+cancellazione+gruppo");
                }

            } else if ("addCaratteristica".equals(action)) {
                Map<String, Object> body = new HashMap<>();
                body.put("nome", request.getParameter("nome"));
                body.put("differenzaPrezzo", Double.parseDouble(request.getParameter("differenzaPrezzo")));
                body.put("isDefault", "true".equals(request.getParameter("isDefault")));
                String idGmcStr = request.getParameter("idGmc");
                if (idGmcStr != null && !idGmcStr.isBlank()) {
                    try {
                        body.put("idGmc", Integer.parseInt(idGmcStr.trim()));
                    } catch (NumberFormatException e) {}
                }

                String jsonBody = mapper.writeValueAsString(body);
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 201 || apiRes.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=created");
                } else {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=error&detail=Errore+creazione+caratteristica");
                }

            } else if ("editCaratteristica".equals(action)) {
                String idCaratteristica = request.getParameter("idCaratteristica");
                Map<String, Object> body = new HashMap<>();
                body.put("nome", request.getParameter("nome"));
                body.put("differenzaPrezzo", Double.parseDouble(request.getParameter("differenzaPrezzo")));
                body.put("isDefault", "true".equals(request.getParameter("isDefault")));
                String idGmcStr = request.getParameter("idGmc");
                if (idGmcStr != null && !idGmcStr.isBlank()) {
                    try {
                        body.put("idGmc", Integer.parseInt(idGmcStr.trim()));
                    } catch (NumberFormatException e) {}
                } else {
                    body.put("idGmc", null);
                }

                String jsonBody = mapper.writeValueAsString(body);
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche/" + idCaratteristica))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 200 || apiRes.statusCode() == 204) {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=updated");
                } else {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=error&detail=Errore+modifica+caratteristica");
                }

            } else if ("deleteCaratteristica".equals(action)) {
                String idCaratteristica = request.getParameter("idCaratteristica");
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche/" + idCaratteristica))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 200 || apiRes.statusCode() == 204) {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=deleted");
                } else {
                    response.sendRedirect(contextPath + "/owner-characteristics?msg=error&detail=Errore+cancellazione+caratteristica");
                }
            } else {
                response.sendRedirect(contextPath + "/owner-characteristics");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/owner-characteristics?msg=error&detail=Eccezione+interna");
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

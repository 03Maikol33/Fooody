package it.maikol.fooodyweb.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.maikol.fooodyweb.models.DettaglioProdotto;
import it.maikol.fooodyweb.models.Ordine;
import it.maikol.fooodyweb.models.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller per la dashboard ordini del Proprietario.
 * Rotta: /owner-orders
 */
public class OwnerOrdersController extends BaseController {

    private static final String API_BASE = "http://localhost:8080/Fooody/api";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        HashMap<String, Object> data = new HashMap<>();

        // Verifica ruolo proprietario
        if (!checkRole(session, response, request, "proprietario")) return;

        Utente utente = (Utente) session.getAttribute("utente");
        String token = (String) session.getAttribute("tokenJWT");
        data.put("utenteLoggato", utente);

        String filtroStato = request.getParameter("stato");
        String filtroDataParam = request.getParameter("dataInserimento");
        String filtroData = (filtroDataParam != null) ? filtroDataParam : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        data.put("filtroStato", filtroStato != null ? filtroStato : "");
        data.put("filtroData", filtroData);

        List<Ordine> ordini = new ArrayList<>();
        String errorMsg = null;

        try {
            StringBuilder url = new StringBuilder(API_BASE + "/ordini");
            List<String> params = new ArrayList<>();
            if (filtroData != null && !filtroData.isBlank()) {
                params.add("dataInserimento=" + URLEncoder.encode(filtroData, StandardCharsets.UTF_8));
            }
            if (filtroStato != null && !filtroStato.isBlank()) {
                params.add("stato=" + URLEncoder.encode(filtroStato, StandardCharsets.UTF_8).replace("+", "%20"));
            }
            if (!params.isEmpty()) {
                url.append("?").append(String.join("&", params));
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url.toString()))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            if (apiResponse.statusCode() == 200) {
                ordini = mapper.readValue(apiResponse.body(), new TypeReference<List<Ordine>>() {});
                for (Ordine ordine : ordini) {
                    try {
                        HttpRequest detReq = HttpRequest.newBuilder()
                                .uri(URI.create(API_BASE + "/ordini/" + ordine.getIdOrdine() + "/dettagli"))
                                .header("Authorization", "Bearer " + token)
                                .GET()
                                .build();
                        HttpResponse<String> detRes = client.send(detReq, HttpResponse.BodyHandlers.ofString());
                        if (detRes.statusCode() == 200) {
                            List<DettaglioProdotto> det = mapper.readValue(detRes.body(), new TypeReference<List<DettaglioProdotto>>() {});
                            ordine.setDettagli(det);
                        }
                        
                        HttpRequest opReq = HttpRequest.newBuilder()
                                .uri(URI.create(API_BASE + "/ordini/" + ordine.getIdOrdine() + "/operatori"))
                                .header("Authorization", "Bearer " + token)
                                .GET()
                                .build();
                        HttpResponse<String> opRes = client.send(opReq, HttpResponse.BodyHandlers.ofString());
                        if (opRes.statusCode() == 200) {
                            List<Map<String, Object>> ops = mapper.readValue(opRes.body(), new TypeReference<List<Map<String, Object>>>() {});
                            ordine.setOperatori(ops);
                        }

                        HttpRequest stReq = HttpRequest.newBuilder()
                                .uri(URI.create(API_BASE + "/ordini/" + ordine.getIdOrdine() + "/stati"))
                                .header("Authorization", "Bearer " + token)
                                .GET()
                                .build();
                        HttpResponse<String> stRes = client.send(stReq, HttpResponse.BodyHandlers.ofString());
                        if (stRes.statusCode() == 200) {
                            List<Map<String, Object>> st = mapper.readValue(stRes.body(), new TypeReference<List<Map<String, Object>>>() {});
                            ordine.setStoricoStati(st);
                        }
                    } catch (Exception ex) {}
                }
            } else {
                errorMsg = "Impossibile caricare gli ordini (status: " + apiResponse.statusCode() + ")";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "Errore di connessione all'API.";
        }

        data.put("ordini", ordini);
        if (errorMsg != null) data.put("errorMsg", errorMsg);

        String msg = request.getParameter("msg");
        if ("advanced".equals(msg)) data.put("successMsg", "Stato ordine aggiornato con successo!");
        if ("cancelled".equals(msg)) data.put("successMsg", "Ordine annullato con successo.");
        if ("error".equals(msg)) data.put("errorMsg", request.getParameter("detail"));

        renderTemplate("owner-orders.ftl", data, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (!checkRole(session, response, request, "proprietario")) return;

        String token = (String) session.getAttribute("tokenJWT");
        String action = request.getParameter("action");
        String idOrdineStr = request.getParameter("idOrdine");

        if (action == null || idOrdineStr == null) {
            response.sendRedirect(contextPath + "/owner-orders");
            return;
        }

        try {
            int idOrdine = Integer.parseInt(idOrdineStr);
            HttpClient client = HttpClient.newHttpClient();

            if ("advance".equals(action)) {
                // avanza lo stato di un ordine
                String nuovoStato = request.getParameter("nuovoStato");
                if (nuovoStato == null) {
                    response.sendRedirect(contextPath + "/owner-orders?msg=error&detail=Stato+non+specificato");
                    return;
                }
                String jsonBody = "{\"nuovoStato\":\"" + nuovoStato + "\"}";
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/ordini/" + idOrdine + "/stati"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

                if (apiResponse.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-orders?msg=advanced");
                } else {
                    response.sendRedirect(contextPath + "/owner-orders?msg=error&detail=Errore+aggiornamento+stato");
                }

            } else if ("cancel".equals(action)) {
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/ordini/" + idOrdine))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();
                HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

                if (apiResponse.statusCode() == 204 || apiResponse.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-orders?msg=cancelled");
                } else {
                    response.sendRedirect(contextPath + "/owner-orders?msg=error&detail=Errore+annullamento+ordine");
                }
            } else {
                response.sendRedirect(contextPath + "/owner-orders");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/owner-orders?msg=error&detail=Errore+interno");
        }
    }

    /** Verifica che l'utente in sessione abbia il ruolo richiesto. */
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

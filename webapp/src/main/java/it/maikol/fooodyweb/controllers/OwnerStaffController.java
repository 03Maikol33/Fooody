package it.maikol.fooodyweb.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.maikol.fooodyweb.models.AuthResponse;
import it.maikol.fooodyweb.models.Personale;
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
 * Controller per la gestione del personale lato proprietario.
 * Rotta: /owner-staff
 */
public class OwnerStaffController extends BaseController {

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

        List<Personale> personaleList = new ArrayList<>();
        String errorMsg = null;

        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/personale"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            if (apiResponse.statusCode() == 200) {
                personaleList = mapper.readValue(apiResponse.body(), new TypeReference<List<Personale>>() {});
            } else {
                errorMsg = "Impossibile caricare il personale (status: " + apiResponse.statusCode() + ")";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "Errore di connessione all'API.";
        }

        data.put("personaleList", personaleList);
        if (errorMsg != null) data.put("errorMsg", errorMsg);

        String msg = request.getParameter("msg");
        if ("hired".equals(msg)) data.put("successMsg", "Nuovo membro del personale registrato con successo!");
        if ("promoted".equals(msg)) data.put("successMsg", "Utente promosso allo staff con successo!");
        if ("fired".equals(msg)) data.put("successMsg", "Membro del personale rimosso con successo.");
        if ("error".equals(msg)) data.put("errorMsg", request.getParameter("detail"));

        renderTemplate("owner-staff.ftl", data, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (!checkRole(session, response, request, "proprietario")) return;

        String token = (String) session.getAttribute("tokenJWT");
        String action = request.getParameter("action");

        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            if ("hire".equals(action)) {
                Map<String, Object> body = new HashMap<>();
                body.put("nome", request.getParameter("nome"));
                body.put("cognome", request.getParameter("cognome"));
                body.put("email", request.getParameter("email"));
                body.put("password", request.getParameter("password"));
                body.put("telefono", request.getParameter("telefono") != null ? request.getParameter("telefono") : "");
                body.put("ruolo", "personale");

                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                String jsonBody = mapper.writeValueAsString(body);
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/auth/register"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 201 || apiRes.statusCode() == 200) {
                    int idNuovoUtente = -1;
                    try {
                        Utente uReg = mapper.readValue(apiRes.body(), Utente.class);
                        if (uReg != null && uReg.getIdUtente() > 0) idNuovoUtente = uReg.getIdUtente();
                    } catch (Exception e) {}

                    if (idNuovoUtente <= 0) {
                        try {
                            Map<String, String> loginMap = Map.of("email", request.getParameter("email"), "password", request.getParameter("password"));
                            HttpRequest loginReq = HttpRequest.newBuilder()
                                    .uri(URI.create(API_BASE + "/auth/login"))
                                    .header("Content-Type", "application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(loginMap)))
                                    .build();
                            HttpResponse<String> loginRes = client.send(loginReq, HttpResponse.BodyHandlers.ofString());
                            if (loginRes.statusCode() == 200) {
                                AuthResponse authRes = mapper.readValue(loginRes.body(), AuthResponse.class);
                                if (authRes != null && authRes.getUtente() != null) idNuovoUtente = authRes.getUtente().getIdUtente();
                            }
                        } catch (Exception e) {}
                    }

                    if (idNuovoUtente > 0) {
                        HttpRequest promReq = HttpRequest.newBuilder()
                                .uri(URI.create(API_BASE + "/personale/" + idNuovoUtente))
                                .header("Authorization", "Bearer " + token)
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build();
                        HttpResponse<String> promRes = client.send(promReq, HttpResponse.BodyHandlers.ofString());
                        if (promRes.statusCode() == 201 || promRes.statusCode() == 200) {
                            response.sendRedirect(contextPath + "/owner-staff?msg=hired");
                        } else {
                            response.sendRedirect(contextPath + "/owner-staff?msg=error&detail=Account+creato+ma+errore+durante+la+promozione+a+personale");
                        }
                    } else {
                        response.sendRedirect(contextPath + "/owner-staff?msg=error&detail=Account+creato+ma+impossibile+rilevare+ID+per+promozione");
                    }
                } else {
                    response.sendRedirect(contextPath + "/owner-staff?msg=error&detail=Errore+registrazione+staff+(email+forse+in+uso)");
                }

            } else if ("promote".equals(action)) {
                String idUtente = request.getParameter("idUtente");
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/personale/" + idUtente))
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 201 || apiRes.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-staff?msg=promoted");
                } else {
                    response.sendRedirect(contextPath + "/owner-staff?msg=error&detail=Utente+non+trovato+o+errore+promozione");
                }

            } else if ("fire".equals(action)) {
                String idPersonale = request.getParameter("idPersonale");
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/personale/" + idPersonale))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 204 || apiRes.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-staff?msg=fired");
                } else {
                    response.sendRedirect(contextPath + "/owner-staff?msg=error&detail=Impossibile+licenziare+il+membro");
                }
            } else {
                response.sendRedirect(contextPath + "/owner-staff");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/owner-staff?msg=error&detail=Errore+interno");
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

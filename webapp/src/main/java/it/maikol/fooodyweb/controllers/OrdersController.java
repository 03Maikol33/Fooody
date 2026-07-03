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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller per la lista ordini del cliente e annullamento.
 */
public class OrdersController extends BaseController {

    private static final String API_BASE = "http://localhost:8080/Fooody/api";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        HashMap<String, Object> data = new HashMap<>();

        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        String token = (String) session.getAttribute("tokenJWT");
        data.put("utenteLoggato", utente);

        // Notifiche email simulate in sessione
        String simEmail = (String) session.getAttribute("simulatedEmail");
        if (simEmail != null) {
            data.put("simulatedEmail", simEmail);
            session.removeAttribute("simulatedEmail");
        }

        List<Ordine> ordini = new ArrayList<>();
        String errorMsg = null;

        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/clienti/" + utente.getIdUtente() + "/ordini"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            Map<Integer, Ordine> ordiniMap = new LinkedHashMap<>();

            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
            if (apiResponse.statusCode() == 200) {
                List<Ordine> list1 = mapper.readValue(apiResponse.body(), new TypeReference<List<Ordine>>() {});
                for (Ordine o : list1) {
                    ordiniMap.put(o.getIdOrdine(), o);
                    if ((utente.getIdCliente() == null || utente.getIdCliente() == 0) && o.getIdCliente() > 0) {
                        utente.setIdCliente(o.getIdCliente());
                        session.setAttribute("utente", utente);
                    }
                }
            }

            try {
                HttpRequest allReq = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/ordini"))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();
                HttpResponse<String> allRes = client.send(allReq, HttpResponse.BodyHandlers.ofString());
                if (allRes.statusCode() == 200) {
                    List<Ordine> list2 = mapper.readValue(allRes.body(), new TypeReference<List<Ordine>>() {});
                    int myClientId = (utente.getIdCliente() != null && utente.getIdCliente() > 0) ? utente.getIdCliente() : utente.getIdUtente();
                    for (Ordine o : list2) {
                        if (o.getIdCliente() == myClientId || o.getIdCliente() == utente.getIdUtente() || ordiniMap.containsKey(o.getIdOrdine())) {
                            ordiniMap.put(o.getIdOrdine(), o);
                        }
                    }
                }
            } catch (Exception ex) {}

            ordini = new ArrayList<>(ordiniMap.values());
            if (ordini.isEmpty() && apiResponse.statusCode() != 200) {
                errorMsg = "Impossibile caricare gli ordini (status: " + apiResponse.statusCode() + ")";
            } else {
                for (Ordine o : ordini) {
                    if ("in consegna".equals(o.getStatoCorrente()) || "in_consegna".equals(o.getStatoCorrente())) {
                        data.put("deliveryEmailAlert", "Il tuo ordine #" + o.getIdOrdine() + " è ora in consegna! Il fattorino sta arrivando.");
                    }
                    try {
                        HttpRequest detReq = HttpRequest.newBuilder()
                                .uri(URI.create(API_BASE + "/ordini/" + o.getIdOrdine() + "/dettagli"))
                                .header("Authorization", "Bearer " + token)
                                .GET()
                                .build();
                        HttpResponse<String> detRes = client.send(detReq, HttpResponse.BodyHandlers.ofString());
                        if (detRes.statusCode() == 200) {
                            List<DettaglioProdotto> det = mapper.readValue(detRes.body(), new TypeReference<List<DettaglioProdotto>>() {});
                            o.setDettagli(det);
                        }
                    } catch (Exception ex) {}
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "Errore di connessione all'API.";
        }

        data.put("ordini", ordini);
        if (errorMsg != null) data.put("errorMsg", errorMsg);

        String sessionSuccess = (String) session.getAttribute("successMsg");
        if (sessionSuccess != null) {
            data.put("successMsg", sessionSuccess);
            session.removeAttribute("successMsg");
        }
        String sessionError = (String) session.getAttribute("errorMsg");
        if (sessionError != null) {
            data.put("errorMsg", sessionError);
            session.removeAttribute("errorMsg");
        }

        String msg = request.getParameter("msg");
        if ("ordered".equals(msg)) data.put("successMsg", "Il tuo ordine è stato inviato con successo!");
        if ("cancelled".equals(msg)) data.put("successMsg", "Ordine annullato correttamente.");
        if ("error".equals(msg)) data.put("errorMsg", request.getParameter("detail"));

        renderTemplate("orders.ftl", data, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String token = (String) session.getAttribute("tokenJWT");
        String action = request.getParameter("action");
        String idOrdineStr = request.getParameter("idOrdine");

        if ("cancel".equals(action) && idOrdineStr != null) {
            try {
                int idOrdine = Integer.parseInt(idOrdineStr);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/ordini/" + idOrdine))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();

                HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

                if (apiResponse.statusCode() == 204 || apiResponse.statusCode() == 200) {
                    session.setAttribute("successMsg", "Ordine annullato correttamente.");
                    response.sendRedirect(contextPath + "/orders");
                } else {
                    session.setAttribute("errorMsg", "Impossibile annullare l'ordine.");
                    response.sendRedirect(contextPath + "/orders");
                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("errorMsg", "Errore di sistema nell'annullamento dell'ordine.");
                response.sendRedirect(contextPath + "/orders");
            }
        } else {
            response.sendRedirect(contextPath + "/orders");
        }
    }
}

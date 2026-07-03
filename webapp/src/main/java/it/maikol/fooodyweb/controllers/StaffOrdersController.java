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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller per la dashboard ordini dello Staff.
 * Rotta: /staff-orders
 * Accesso: staff e proprietari.
 *
 * GET  /staff-orders : lista ordini attivi (esclusi "consegnato" e "annullato")
 * POST /staff-orders?action=advance : avanza lo stato di un ordine
 */
public class StaffOrdersController extends BaseController {

    private static final String API_BASE = "http://localhost:8080/Fooody/api";

    private static final List<String> STATI_SEQUENZA = List.of(
            "inserito", "in_preparazione", "pronto", "in_consegna", "consegnato"
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        HashMap<String, Object> data = new HashMap<>();

        if (!checkRoleStaffOrOwner(session, response, request)) return;

        Utente utente = (Utente) session.getAttribute("utente");
        String token = (String) session.getAttribute("tokenJWT");
        data.put("utenteLoggato", utente);

        String filtroStato = request.getParameter("filtro");
        String filtroDataParam = request.getParameter("dataInserimento");
        String filtroData = (filtroDataParam != null) ? filtroDataParam : java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        data.put("filtroStato", filtroStato != null ? filtroStato : "all");
        data.put("filtroData", filtroData);

        List<Ordine> ordini = new ArrayList<>();
        String errorMsg = null;

        try {
            StringBuilder url = new StringBuilder(API_BASE + "/ordini");
            List<String> params = new ArrayList<>();
            if (filtroData != null && !filtroData.isBlank()) {
                params.add("dataInserimento=" + URLEncoder.encode(filtroData, StandardCharsets.UTF_8));
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
                List<Ordine> tutti = mapper.readValue(apiResponse.body(), new TypeReference<List<Ordine>>() {});

                //lo staff vede solo gli ordini attivi
                ordini = tutti.stream()
                        .filter(o -> !"consegnato".equals(o.getStatoCorrente())
                                  && !"annullato".equals(o.getStatoCorrente()))
                        .collect(Collectors.toList());

                if (filtroStato != null && !filtroStato.equals("all")) {
                    final String fs = filtroStato;
                    ordini = ordini.stream()
                            .filter(o -> fs.equals(o.getStatoCorrente()))
                            .collect(Collectors.toList());
                }

                if (filtroData != null && !filtroData.isBlank()) {
                    final String fd = filtroData;
                    ordini = ordini.stream()
                            .filter(o -> o.getTimeInserimento() != null && o.getTimeInserimento().startsWith(fd))
                            .collect(Collectors.toList());
                }

                //carica i dettagli di ogni ordine
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
                    } catch (Exception ex) {
                    }
                }
            } else {
                errorMsg = "Impossibile caricare gli ordini (status: " + apiResponse.statusCode() + ")";
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "Errore di connessione all'API.";
        }

        data.put("ordini", ordini);
        data.put("statiSequenza", STATI_SEQUENZA);
        if (errorMsg != null) data.put("errorMsg", errorMsg);

        String msg = request.getParameter("msg");
        if ("advanced".equals(msg)) data.put("successMsg", "Stato aggiornato con successo!");
        if ("error".equals(msg)) data.put("errorMsg", request.getParameter("detail"));

        renderTemplate("staff-orders.ftl", data, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (!checkRoleStaffOrOwner(session, response, request)) return;

        String token = (String) session.getAttribute("tokenJWT");
        String action = request.getParameter("action");
        String idOrdineStr = request.getParameter("idOrdine");
        String nuovoStato = request.getParameter("nuovoStato");

        if (!"advance".equals(action) || idOrdineStr == null || nuovoStato == null) {
            response.sendRedirect(contextPath + "/staff-orders");
            return;
        }

        try {
            int idOrdine = Integer.parseInt(idOrdineStr);
            String jsonBody = "{\"nuovoStato\":\"" + nuovoStato + "\"}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/ordini/" + idOrdine + "/stati"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            if (apiResponse.statusCode() == 200) {
                response.sendRedirect(contextPath + "/staff-orders?msg=advanced");
            } else {
                response.sendRedirect(contextPath + "/staff-orders?msg=error&detail=Errore+aggiornamento+stato");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/staff-orders?msg=error&detail=Errore+interno");
        }
    }

    /**
     * Restituisce il prossimo stato nella sequenza (null se già all'ultimo).
     * Metodo statico usabile anche nel template tramite reflection (non disponibile in FreeMarker).
     * Per comodità nel template passiamo la lista STATI_SEQUENZA.
     */
    public static String getProssimoStato(String statoCorrente) {
        int idx = STATI_SEQUENZA.indexOf(statoCorrente);
        if (idx >= 0 && idx < STATI_SEQUENZA.size() - 1) {
            return STATI_SEQUENZA.get(idx + 1);
        }
        return null;
    }

    private boolean checkRoleStaffOrOwner(HttpSession session, HttpServletResponse response,
                                          HttpServletRequest request) throws IOException {
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        Utente u = (Utente) session.getAttribute("utente");
        if (!"personale".equals(u.getRuolo()) && !"staff".equals(u.getRuolo()) && !"proprietario".equals(u.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}

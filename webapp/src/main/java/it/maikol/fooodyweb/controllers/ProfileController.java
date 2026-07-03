package it.maikol.fooodyweb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Controller per la pagina profilo utente e modifica.
 * /profile
 */
public class ProfileController extends BaseController {

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
        data.put("utente", utente);
        data.put("utenteLoggato", utente);

        String iniziale = "?";
        if (utente.getNome() != null && !utente.getNome().isEmpty()) {
            iniziale = String.valueOf(utente.getNome().charAt(0)).toUpperCase();
        }
        data.put("inizialeAvatar", iniziale);

        String msg = request.getParameter("msg");
        if ("updated".equals(msg)) data.put("successMsg", "Dati profilo aggiornati con successo!");
        if ("error".equals(msg)) data.put("errorMsg", request.getParameter("detail"));

        renderTemplate("profile.ftl", data, response);
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

        Utente utente = (Utente) session.getAttribute("utente");
        String token = (String) session.getAttribute("tokenJWT");

        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String telefono = request.getParameter("telefono");
        String via = request.getParameter("via");
        String civico = request.getParameter("civico");
        String citta = request.getParameter("citta");

        try {
            Map<String, Object> reqBody = new HashMap<>();
            reqBody.put("idUtente", utente.getIdUtente());
            reqBody.put("nome", nome);
            reqBody.put("cognome", cognome);
            reqBody.put("email", utente.getEmail());
            reqBody.put("telefono", telefono != null ? telefono : "");
            reqBody.put("via", via != null ? via : "");
            reqBody.put("civico", civico != null ? civico : "");
            reqBody.put("citta", citta != null ? citta : "");
            reqBody.put("ruolo", utente.getRuolo());

            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(reqBody);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/clienti/" + utente.getIdUtente()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            if (apiResponse.statusCode() == 200 || apiResponse.statusCode() == 204) {
                // Aggiorna utente in sessione
                utente.setNome(nome);
                utente.setCognome(cognome);
                session.setAttribute("utente", utente);
                response.sendRedirect(contextPath + "/profile?msg=updated");
            } else {
                response.sendRedirect(contextPath + "/profile?msg=error&detail=Impossibile+aggiornare+i+dati");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/profile?msg=error&detail=Errore+di+connessione");
        }
    }
}

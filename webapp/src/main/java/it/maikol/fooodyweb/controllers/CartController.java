package it.maikol.fooodyweb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.maikol.fooodyweb.models.CartItem;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller per il Carrello e conferma Ordine.
 */
public class CartController extends BaseController {

    private static final String CART_SESSION_KEY = "carrello";
    private static final String API_BASE = "http://localhost:8080/Fooody/api";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        HashMap<String, Object> data = new HashMap<>();

        List<CartItem> carrello = getCart(session);
        data.put("carrello", carrello);

        // Calcola totale e tempo preparazione
        double totale = carrello.stream().mapToDouble(CartItem::getSubtotale).sum();
        int tempoPrepTotale = carrello.stream().mapToInt(CartItem::getTempoPreparazione).sum();
        if (tempoPrepTotale == 0 && !carrello.isEmpty()) tempoPrepTotale = 20;

        data.put("totale", String.format("%.2f", totale));
        data.put("tempoPrepTotale", tempoPrepTotale);
        data.put("carrelloVuoto", carrello.isEmpty());

        if (session != null && session.getAttribute("utente") != null) {
            data.put("utenteLoggato", session.getAttribute("utente"));
        }

        String msg = request.getParameter("msg");
        if ("added".equals(msg)) data.put("successMsg", "Prodotto aggiunto al carrello!");
        if ("removed".equals(msg)) data.put("successMsg", "Prodotto rimosso dal carrello.");
        if ("cleared".equals(msg)) data.put("successMsg", "Carrello svuotato.");
        if ("error".equals(msg)) data.put("errorMsg", request.getParameter("detail"));

        renderTemplate("cart.ftl", data, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(true);
        String contextPath = request.getContextPath();

        if (action == null) {
            response.sendRedirect(contextPath + "/cart");
            return;
        }

        switch (action) {
            case "add" -> handleAdd(request, response, session, contextPath);
            case "remove" -> handleRemove(request, response, session, contextPath);
            case "clear" -> handleClear(response, session, contextPath);
            case "confirm" -> handleConfirm(request, response, session, contextPath);
            default -> response.sendRedirect(contextPath + "/cart");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response,
                           HttpSession session, String contextPath) throws IOException {
        try {
            int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
            int quantita = Integer.parseInt(request.getParameter("quantita"));
            String nomeProdotto = request.getParameter("nomeProdotto");
            double prezzoBase = Double.parseDouble(request.getParameter("prezzoBase"));
            String immagine = request.getParameter("immagine");

            int tempoPreparazione = 15;
            try { tempoPreparazione = Integer.parseInt(request.getParameter("tempoPreparazione")); } catch (NumberFormatException ignore) {}
            
            List<Integer> caratteristicheScelte = new ArrayList<>();
            //tutti i parametri
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                //se ho una caratteristica
                if (entry.getKey().startsWith("caratteristica")) {
                    for (String fp : entry.getValue()) {
                        try { 
                            caratteristicheScelte.add(Integer.parseInt(fp)); 
                        } catch (NumberFormatException ignore) {}
                    }
                }
            }

            String[] nomiFeatParams = request.getParameterValues("nomeCaratteristica");
            List<String> nomiCaratteristicheScelte = new ArrayList<>();
            if (nomiFeatParams != null) {
                for (String nfp : nomiFeatParams) {
                    nomiCaratteristicheScelte.add(nfp);
                }
            }

            List<CartItem> carrello = getCart(session);

            boolean trovato = false;
            for (CartItem item : carrello) {
                if (item.getIdProdotto() == idProdotto &&
                    item.getCaratteristicheScelte().equals(caratteristicheScelte)) {
                    item.setQuantita(item.getQuantita() + quantita);
                    trovato = true;
                    break;
                }
            }

            if (!trovato) {
                carrello.add(new CartItem(idProdotto, nomeProdotto, prezzoBase, immagine, quantita, caratteristicheScelte, nomiCaratteristicheScelte, tempoPreparazione));
            }

            session.setAttribute(CART_SESSION_KEY, carrello);
            response.sendRedirect(contextPath + "/cart?msg=added");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/cart?msg=error&detail=Errore+nell'aggiunta+al+carrello");
        }
    }

    private void handleRemove(HttpServletRequest request, HttpServletResponse response,
                              HttpSession session, String contextPath) throws IOException {
        try {
            int index = Integer.parseInt(request.getParameter("index"));
            List<CartItem> carrello = getCart(session);
            if (index >= 0 && index < carrello.size()) {
                carrello.remove(index);
                session.setAttribute(CART_SESSION_KEY, carrello);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        response.sendRedirect(contextPath + "/cart?msg=removed");
    }

    private void handleClear(HttpServletResponse response, HttpSession session,
                             String contextPath) throws IOException {
        session.setAttribute(CART_SESSION_KEY, new ArrayList<CartItem>());
        response.sendRedirect(contextPath + "/cart?msg=cleared");
    }

    private void handleConfirm(HttpServletRequest request, HttpServletResponse response,
                               HttpSession session, String contextPath) throws IOException {
        Utente utente = (Utente) session.getAttribute("utente");
        String token = (String) session.getAttribute("tokenJWT");

        if (utente == null || token == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        List<CartItem> carrello = getCart(session);
        if (carrello.isEmpty()) {
            response.sendRedirect(contextPath + "/cart?msg=error&detail=Carrello+vuoto");
            return;
        }

        String orarioStr = request.getParameter("orarioConsegna");
        if (orarioStr == null || orarioStr.isBlank()) {
            response.sendRedirect(contextPath + "/cart?msg=error&detail=Specifica+l'orario+di+consegna");
            return;
        }

        try {
            LocalTime orarioScelto = LocalTime.parse(orarioStr);
            int tempoPrepTotale = carrello.stream().mapToInt(CartItem::getTempoPreparazione).sum();
            if (tempoPrepTotale == 0) tempoPrepTotale = 20;

            LocalTime minimoPossibile = LocalTime.now().plusMinutes(tempoPrepTotale);
            LocalTime chiusura = LocalTime.of(23, 30);

            if (orarioScelto.isBefore(minimoPossibile)) {
                response.sendRedirect(contextPath + "/cart?msg=error&detail=Orario+troppo+presto.+Tempo+prep.+minimo:+"+tempoPrepTotale+"+min");
                return;
            }
            if (orarioScelto.isAfter(chiusura)) {
                response.sendRedirect(contextPath + "/cart?msg=error&detail=L'orario+supera+l'orario+di+chiusura+(23:30)");
                return;
            }

            String dateTimeConsegna = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + orarioStr + ":00";

            List<Map<String, Object>> itemsList = new ArrayList<>();
            for (CartItem ci : carrello) {
                Map<String, Object> det = new HashMap<>();
                det.put("idProdotto", ci.getIdProdotto());
                det.put("quantita", ci.getQuantita());
                det.put("caratteristicheScelte", ci.getCaratteristicheScelte());
                itemsList.add(det);
            }

            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            if (utente.getIdCliente() == null || utente.getIdCliente() == 0) {
                try {
                    HttpRequest discoverReq = HttpRequest.newBuilder()
                            .uri(URI.create(API_BASE + "/clienti/" + utente.getIdUtente() + "/ordini"))
                            .header("Authorization", "Bearer " + token)
                            .GET().build();
                    HttpResponse<String> discoverRes = client.send(discoverReq, HttpResponse.BodyHandlers.ofString());
                    if (discoverRes.statusCode() == 200) {
                        List<it.maikol.fooodyweb.models.Ordine> pastOrders = mapper.readValue(discoverRes.body(), new com.fasterxml.jackson.core.type.TypeReference<List<it.maikol.fooodyweb.models.Ordine>>() {});
                        for (it.maikol.fooodyweb.models.Ordine po : pastOrders) {
                            if (po.getIdCliente() > 0) {
                                utente.setIdCliente(po.getIdCliente());
                                session.setAttribute("utente", utente);
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {}
            }

            Map<String, Object> reqBody = new HashMap<>();
            int clientId = utente.getIdUtente();
            reqBody.put("idCliente", clientId);
            reqBody.put("orarioConsegnaRichiesto", dateTimeConsegna);
            reqBody.put("items", itemsList);

            String jsonBody = mapper.writeValueAsString(reqBody);

            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/ordini"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            if (apiResponse.statusCode() == 200 || apiResponse.statusCode() == 201) {
                // Svuota carrello
                session.setAttribute(CART_SESSION_KEY, new ArrayList<CartItem>());

                //invio email
                String simEmailMsg = "Email di conferma dell'ordine inviata a " + utente.getEmail() + 
                                     ": Abbiamo ricevuto il tuo ordine! Consegna stimata per le " + orarioStr;
                System.out.println(simEmailMsg);
                session.setAttribute("simulatedEmail", simEmailMsg);
                session.setAttribute("successMsg", "Il tuo ordine è stato inviato con successo!");

                response.sendRedirect(contextPath + "/orders");
            } else {
                response.sendRedirect(contextPath + "/cart?msg=error&detail=Errore+creazione+ordine:+" + apiResponse.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/cart?msg=error&detail=Errore+interno");
        }
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        if (session == null) return new ArrayList<>();
        Object cartObj = session.getAttribute(CART_SESSION_KEY);
        if (cartObj instanceof List) {
            return (List<CartItem>) cartObj;
        }
        List<CartItem> newCart = new ArrayList<>();
        session.setAttribute(CART_SESSION_KEY, newCart);
        return newCart;
    }
}

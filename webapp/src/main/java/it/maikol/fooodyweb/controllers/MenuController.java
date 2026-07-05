package it.maikol.fooodyweb.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.maikol.fooodyweb.models.CartItem;
import it.maikol.fooodyweb.models.Prodotto;
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
import java.util.stream.Collectors;

/**
 * Controller per la pagina Menu.
 * Carica i prodotti dall'API e li passa al template FreeMarker.
 * Supporta filtri per categoria e ricerca per nome tramite query string.
 */
public class MenuController extends BaseController {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HashMap<String, Object> data = new HashMap<>();
        data.put("context", request.getContextPath());

        //ottengo la sesione
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("utente") != null) {
            Utente utente = (Utente) session.getAttribute("utente");
            data.put("utenteLoggato", utente);
            //calcolo # item nel carr.
            List<CartItem> carrello = getCart(session);
            int cartCount = carrello.stream().mapToInt(CartItem::getQuantita).sum();
            data.put("cartCount", cartCount);
        }

        //verifica la presenza di filtri nella query string
        String filtroCategoria = request.getParameter("categoria");
        String filtroNome = request.getParameter("nome");
        data.put("filtroCategoria", filtroCategoria != null ? filtroCategoria : "");
        data.put("filtroNome", filtroNome != null ? filtroNome : "");

        //ottenimento delle categorie di prodotti esistenti
        List<String> categorieList = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest catRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/Fooody/api/prodotti/categorie"))
                    .GET()
                    .build();
            HttpResponse<String> catResponse = client.send(catRequest, HttpResponse.BodyHandlers.ofString());
            if (catResponse.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(catResponse.body());
                if (rootNode != null && rootNode.isArray()) {
                    for (JsonNode node : rootNode) {
                        String cat = null;
                        if (node.isTextual()) {
                            cat = node.asText();
                        } else if (node.isObject()) {
                            if (node.has("nome")) cat = node.get("nome").asText();
                            else if (node.has("categoria")) cat = node.get("categoria").asText();
                            else if (node.has("val")) cat = node.get("val").asText();
                        }
                        if (cat != null && !cat.isBlank() && !categorieList.contains(cat)) {
                            categorieList.add(cat);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // otteminemnto prodotti
        List<Prodotto> prodotti = new ArrayList<>();
        try {
            StringBuilder urlBuilder = new StringBuilder("http://localhost:8080/Fooody/api/prodotti");
            boolean first = true;
            if (filtroCategoria != null && !filtroCategoria.isBlank()) {
                urlBuilder.append("?categoria=").append(filtroCategoria);
                first = false;
            }
            if (filtroNome != null && !filtroNome.isBlank()) {
                urlBuilder.append(first ? "?" : "&").append("nome=").append(filtroNome);
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(urlBuilder.toString()))
                    .GET()
                    .build();

            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

            if (apiResponse.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                prodotti = mapper.readValue(apiResponse.body(), new TypeReference<List<Prodotto>>() {});
                String token = (session != null) ? (String) session.getAttribute("tokenJWT") : null;
                popolaIngredienti(prodotti, token);
            } else {
                data.put("errorMsg", "Impossibile caricare il menu al momento.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            data.put("errorMsg", "Errore di connessione al server.");
        }

        if (categorieList.isEmpty() && (filtroCategoria == null || filtroCategoria.isBlank())) {
            categorieList = prodotti.stream()
                    .map(Prodotto::getCategoria)
                    .filter(c -> c != null && !c.isBlank())
                    .distinct()
                    .collect(Collectors.toList());
        }

        data.put("categorieList", categorieList);
        data.put("prodotti", prodotti);
        renderTemplate("menu.ftl", data, response);
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        Object cartObj = session.getAttribute("carrello");
        if (cartObj instanceof List) return (List<CartItem>) cartObj;
        return new ArrayList<>();
    }
}


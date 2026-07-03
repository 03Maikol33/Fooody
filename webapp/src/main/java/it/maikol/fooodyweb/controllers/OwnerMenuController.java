package it.maikol.fooodyweb.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.maikol.fooodyweb.models.Caratteristica;
import it.maikol.fooodyweb.models.GruppoGmc;
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
import java.util.Map;

/**
 * Controller per la gestione del menu lato proprietario.
 */
public class OwnerMenuController extends BaseController {

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

        List<Prodotto> prodotti = new ArrayList<>();
        String errorMsg = null;

        String filtroCategoria = request.getParameter("categoria");
        data.put("filtroCategoria", filtroCategoria != null ? filtroCategoria : "");

        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            StringBuilder urlProd = new StringBuilder(API_BASE + "/prodotti");
            if (filtroCategoria != null && !filtroCategoria.isBlank()) {
                urlProd.append("?categoria=").append(filtroCategoria);
            }

            HttpRequest apiRequest = HttpRequest.newBuilder()
                    .uri(URI.create(urlProd.toString()))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
            if (apiResponse.statusCode() == 200) {
                prodotti = mapper.readValue(apiResponse.body(), new TypeReference<List<Prodotto>>() {});
                popolaIngredienti(prodotti, token);
            } else {
                errorMsg = "Impossibile caricare i prodotti (status: " + apiResponse.statusCode() + ")";
            }

            List<String> categorieList = new ArrayList<>();
            try {
                HttpRequest catReq = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/prodotti/categorie"))
                        .GET().build();
                HttpResponse<String> catRes = client.send(catReq, HttpResponse.BodyHandlers.ofString());
                if (catRes.statusCode() == 200) {
                    com.fasterxml.jackson.databind.JsonNode rootNode = mapper.readTree(catRes.body());
                    if (rootNode != null && rootNode.isArray()) {
                        for (com.fasterxml.jackson.databind.JsonNode node : rootNode) {
                            String cat = null;
                            if (node.isTextual()) cat = node.asText();
                            else if (node.isObject()) {
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
            } catch (Exception ex) {}
            if (categorieList.isEmpty() && (filtroCategoria == null || filtroCategoria.isBlank())) {
                categorieList = prodotti.stream()
                        .map(Prodotto::getCategoria)
                        .filter(c -> c != null && !c.isBlank())
                        .distinct()
                        .collect(java.util.stream.Collectors.toList());
            }
            data.put("categorieList", categorieList);

            List<GruppoGmc> gruppi = new ArrayList<>();
            List<Caratteristica> caratteristiche = new ArrayList<>();
            try {
                HttpRequest gReq = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche/gruppi"))
                        .header("Authorization", "Bearer " + token)
                        .GET().build();
                HttpResponse<String> gRes = client.send(gReq, HttpResponse.BodyHandlers.ofString());
                if (gRes.statusCode() == 200) {
                    gruppi = mapper.readValue(gRes.body(), new TypeReference<List<GruppoGmc>>() {});
                }
            } catch (Exception ex) {}
            try {
                HttpRequest cReq = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/caratteristiche"))
                        .header("Authorization", "Bearer " + token)
                        .GET().build();
                HttpResponse<String> cRes = client.send(cReq, HttpResponse.BodyHandlers.ofString());
                if (cRes.statusCode() == 200) {
                    caratteristiche = mapper.readValue(cRes.body(), new TypeReference<List<Caratteristica>>() {});
                }
            } catch (Exception ex) {}
            List<it.maikol.fooodyweb.models.Ingrediente> tuttiIng = new ArrayList<>();
            try {
                HttpRequest iReq = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/ingredienti"))
                        .header("Authorization", "Bearer " + token)
                        .GET().build();
                HttpResponse<String> iRes = client.send(iReq, HttpResponse.BodyHandlers.ofString());
                if (iRes.statusCode() == 200) {
                    tuttiIng = mapper.readValue(iRes.body(), new TypeReference<List<it.maikol.fooodyweb.models.Ingrediente>>() {});
                }
            } catch (Exception ex) {}
            data.put("tuttiGruppi", gruppi);
            data.put("tutteCaratteristiche", caratteristiche);
            data.put("tuttiIngredienti", tuttiIng);
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "Errore di connessione all'API.";
        }

        data.put("prodotti", prodotti);
        if (errorMsg != null) data.put("errorMsg", errorMsg);

        String msg = request.getParameter("msg");
        if ("added".equals(msg)) data.put("successMsg", "Prodotto aggiunto con successo!");
        if ("updated".equals(msg)) data.put("successMsg", "Prodotto aggiornato con successo!");
        if ("featAdded".equals(msg)) data.put("successMsg", "Caratteristica aggiunta con successo!");
        if ("removed".equals(msg)) data.put("successMsg", "Caratteristica rimossa con successo.");
        if ("ingAdded".equals(msg)) data.put("successMsg", "Ingrediente associato al prodotto con successo!");
        if ("ingRemoved".equals(msg)) data.put("successMsg", "Ingrediente rimosso dal prodotto.");
        if ("error".equals(msg)) data.put("errorMsg", request.getParameter("detail"));

        renderTemplate("owner-menu.ftl", data, response);
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

            if ("addProdotto".equals(action)) {
                Map<String, Object> body = new HashMap<>();
                body.put("nome", request.getParameter("nome"));
                body.put("categoria", request.getParameter("categoria"));
                body.put("descrizione", request.getParameter("descrizione"));
                body.put("prezzoBase", Double.parseDouble(request.getParameter("prezzoBase")));
                body.put("tempoPreparazione", Integer.parseInt(request.getParameter("tempoPreparazione")));
                body.put("procedura", request.getParameter("procedura") != null ? request.getParameter("procedura") : "");
                body.put("immagine", "");

                String jsonBody = mapper.writeValueAsString(body);
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/prodotti"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 201 || apiRes.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-menu?msg=added");
                } else {
                    response.sendRedirect(contextPath + "/owner-menu?msg=error&detail=Errore+aggiunta+prodotto");
                }

            } else if ("updateProdotto".equals(action)) {
                String id = request.getParameter("idProdotto");
                Map<String, Object> body = new HashMap<>();
                body.put("idProdotto", Integer.parseInt(id));
                body.put("nome", request.getParameter("nome"));
                body.put("categoria", request.getParameter("categoria"));
                body.put("descrizione", request.getParameter("descrizione"));
                body.put("prezzoBase", Double.parseDouble(request.getParameter("prezzoBase")));
                body.put("tempoPreparazione", Integer.parseInt(request.getParameter("tempoPreparazione")));
                body.put("procedura", request.getParameter("procedura") != null ? request.getParameter("procedura") : "");

                String jsonBody = mapper.writeValueAsString(body);
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/prodotti/" + id))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 200 || apiRes.statusCode() == 204) {
                    response.sendRedirect(contextPath + "/owner-menu?msg=updated");
                } else {
                    response.sendRedirect(contextPath + "/owner-menu?msg=error&detail=Errore+modifica+prodotto");
                }

            } else if ("assignExistingCaratteristica".equals(action)) {
                String idProdotto = request.getParameter("idProdotto");
                String itemToAssign = request.getParameter("itemToAssign");
                if (idProdotto != null && itemToAssign != null) {
                    List<Caratteristica> allChars = new ArrayList<>();
                    try {
                        HttpRequest cReq = HttpRequest.newBuilder()
                                .uri(URI.create(API_BASE + "/caratteristiche"))
                                .header("Authorization", "Bearer " + token)
                                .GET().build();
                        HttpResponse<String> cRes = client.send(cReq, HttpResponse.BodyHandlers.ofString());
                        if (cRes.statusCode() == 200) {
                            allChars = mapper.readValue(cRes.body(), new TypeReference<List<Caratteristica>>() {});
                        }
                    } catch (Exception ex) {}

                    List<Caratteristica> toAdd = new ArrayList<>();
                    if (itemToAssign.startsWith("CHAR_")) {
                        int cid = Integer.parseInt(itemToAssign.substring(5));
                        for (Caratteristica c : allChars) {
                            if (c.getIdCaratteristica() == cid) {
                                toAdd.add(c);
                                break;
                            }
                        }
                    } else if (itemToAssign.startsWith("GMC_")) {
                        int gid = Integer.parseInt(itemToAssign.substring(4));
                        for (Caratteristica c : allChars) {
                            if (c.getIdGmc() != null && c.getIdGmc() == gid) {
                                toAdd.add(c);
                            }
                        }
                    }

                    boolean anySuccess = false;
                    for (Caratteristica c : toAdd) {
                        try {
                            Map<String, Integer> payloadAppoggio = new HashMap<>();
                            payloadAppoggio.put("idCaratteristica", c.getIdCaratteristica());
                            String jsonBody = mapper.writeValueAsString(payloadAppoggio);
                            HttpRequest apiReq = HttpRequest.newBuilder()
                                    .uri(URI.create(API_BASE + "/prodotti/" + idProdotto + "/caratteristiche"))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + token)
                                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                                    .build();
                            HttpResponse<String> apiRes = client.send(apiReq, HttpResponse.BodyHandlers.ofString());
                            if (apiRes.statusCode() == 200 || apiRes.statusCode() == 201) {
                                anySuccess = true;
                            }
                        } catch (Exception ex) {}
                    }
                    if (anySuccess) {
                        response.sendRedirect(contextPath + "/owner-menu?msg=featAdded");
                    } else {
                        response.sendRedirect(contextPath + "/owner-menu?msg=error&detail=Impossibile+associare+l'elemento");
                    }
                    return;
                }

            } else if ("addCaratteristica".equals(action)) {
                String idProdotto = request.getParameter("idProdotto");
                Map<String, Object> body = new HashMap<>();
                body.put("nome", request.getParameter("nome"));
                body.put("differenzaPrezzo", Double.parseDouble(request.getParameter("differenzaPrezzo")));
                body.put("isDefault", "true".equals(request.getParameter("isDefault")));
                String gruppo = request.getParameter("gruppo");
                if (gruppo != null && !gruppo.isBlank()) {
                    body.put("descrizione", gruppo);
                    try {
                        body.put("idGmc", Integer.parseInt(gruppo.trim()));
                    } catch (NumberFormatException e) {}
                }

                String jsonBody = mapper.writeValueAsString(body);
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/prodotti/" + idProdotto + "/caratteristiche"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> apiRes = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                if (apiRes.statusCode() == 201 || apiRes.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-menu?msg=featAdded");
                } else {
                    response.sendRedirect(contextPath + "/owner-menu?msg=error&detail=Errore+aggiunta+caratteristica");
                }

            } else if ("removeCaratteristica".equals(action)) {
                String idProdottoStr = request.getParameter("idProdotto");
                String idCaratteristicaStr = request.getParameter("idCaratteristica");

                if (idProdottoStr == null || idCaratteristicaStr == null) {
                    response.sendRedirect(contextPath + "/owner-menu?msg=error&detail=Parametri+mancanti");
                    return;
                }

                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_BASE + "/prodotti/" + idProdottoStr + "/caratteristiche/" + idCaratteristicaStr))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();

                HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());

                if (apiResponse.statusCode() == 204 || apiResponse.statusCode() == 200) {
                    response.sendRedirect(contextPath + "/owner-menu?msg=removed");
                } else {
                    response.sendRedirect(contextPath + "/owner-menu?msg=error&detail=Caratteristica+non+trovata");
                }
            } else if ("assignExistingIngrediente".equals(action)) {
                String idProdottoStr = request.getParameter("idProdotto");
                String idIngredienteStr = request.getParameter("idIngrediente");
                String quantita = request.getParameter("quantita");
                if (idProdottoStr != null && idIngredienteStr != null) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("idIngrediente", Integer.parseInt(idIngredienteStr));
                    body.put("quantita", quantita != null ? quantita.trim() : "q.b.");

                    String jsonBody = mapper.writeValueAsString(body);
                    HttpRequest apiReq = HttpRequest.newBuilder()
                            .uri(URI.create(API_BASE + "/prodotti/" + idProdottoStr + "/ingredienti"))
                            .header("Content-Type", "application/json")
                            .header("Authorization", "Bearer " + token)
                            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                            .build();
                    HttpResponse<String> apiRes = client.send(apiReq, HttpResponse.BodyHandlers.ofString());
                    if (apiRes.statusCode() == 200 || apiRes.statusCode() == 201) {
                        response.sendRedirect(contextPath + "/owner-menu?msg=ingAdded");
                    } else {
                        response.sendRedirect(contextPath + "/owner-menu?msg=error&detail=Impossibile+associare+ingrediente");
                    }
                    return;
                }
            } else if ("removeIngrediente".equals(action)) {
                String idProdottoStr = request.getParameter("idProdotto");
                String idIngredienteStr = request.getParameter("idIngrediente");
                if (idProdottoStr != null && idIngredienteStr != null) {
                    HttpRequest apiReq = HttpRequest.newBuilder()
                            .uri(URI.create(API_BASE + "/prodotti/" + idProdottoStr + "/ingredienti/" + idIngredienteStr))
                            .header("Authorization", "Bearer " + token)
                            .DELETE()
                            .build();
                    HttpResponse<String> apiRes = client.send(apiReq, HttpResponse.BodyHandlers.ofString());
                    if (apiRes.statusCode() == 204 || apiRes.statusCode() == 200) {
                        response.sendRedirect(contextPath + "/owner-menu?msg=ingRemoved");
                    } else {
                        response.sendRedirect(contextPath + "/owner-menu?msg=error&detail=Errore+rimozione+ingrediente");
                    }
                    return;
                }
            } else {
                response.sendRedirect(contextPath + "/owner-menu");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/owner-menu?msg=error&detail=Errore+interno");
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

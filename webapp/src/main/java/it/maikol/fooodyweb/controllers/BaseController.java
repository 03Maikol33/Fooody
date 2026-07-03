/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooodyweb.controllers;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
/**
 *
 * @author Maikol
 * Controller base inizializza FreeMarker.
 * Tutte le  Servlet estenderanno.
 */
public class BaseController extends HttpServlet{
    protected Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        try {
            
            String templatePath = getServletContext().getRealPath("/WEB-INF/templates");
            
            freemarkerConfig.setDirectoryForTemplateLoading(new java.io.File(templatePath));
            
        } catch (IOException e) {
            throw new ServletException("Impossibile configurare la cartella dei template FreeMarker", e);
        }

        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
    }

    /**
     * Metodo di supporto per stampare a video un template
     */
    protected void renderTemplate(String templateName, Map<String, Object> data, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        try {
            String contextPath = getServletContext().getContextPath();
            data.put("context", contextPath);
            //file ftl
            Template template = freemarkerConfig.getTemplate(templateName);
            //inietta i dati e trasforma in HTML
            template.process(data, response.getWriter());
        } catch (Exception e) {
            throw new IOException("Errore nel rendering del template: " + templateName, e);
        }
    }

    /**
     * Metodo di supporto per popola la lista degli ingredienti per i prodotti dal server API
     */
    protected void popolaIngredienti(java.util.List<it.maikol.fooodyweb.models.Prodotto> prodotti, String token) {
        if (prodotti == null || prodotti.isEmpty()) return;
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            for (it.maikol.fooodyweb.models.Prodotto p : prodotti) {
                try {
                    java.net.http.HttpRequest.Builder reqBuilder = java.net.http.HttpRequest.newBuilder()
                            .uri(java.net.URI.create("http://localhost:8080/Fooody/api/prodotti/" + p.getIdProdotto() + "/ingredienti"))
                            .GET();
                    if (token != null && !token.isBlank()) {
                        reqBuilder.header("Authorization", "Bearer " + token);
                    }
                    java.net.http.HttpResponse<String> res = client.send(reqBuilder.build(), java.net.http.HttpResponse.BodyHandlers.ofString());
                    if (res.statusCode() == 200) {
                        java.util.List<it.maikol.fooodyweb.models.Ingrediente> ingList = mapper.readValue(res.body(), new com.fasterxml.jackson.core.type.TypeReference<java.util.List<it.maikol.fooodyweb.models.Ingrediente>>() {});
                        p.setIngredienti(ingList);
                    } else {
                        p.setIngredienti(new java.util.ArrayList<>());
                    }
                } catch (Exception ex) {
                    p.setIngredienti(new java.util.ArrayList<>());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooodyweb.controllers;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import it.maikol.fooodyweb.models.Ingrediente;
import it.maikol.fooodyweb.models.Prodotto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
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
            
            freemarkerConfig.setDirectoryForTemplateLoading(new File(templatePath));
            
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
    protected void popolaIngredienti(List<Prodotto> prodotti, String token) {
        if (prodotti == null || prodotti.isEmpty()) return;
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            for (Prodotto p : prodotti) {
                try {
                    HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/Fooody/api/prodotti/" + p.getIdProdotto() + "/ingredienti"))
                            .GET();
                    if (token != null && !token.isBlank()) {
                        reqBuilder.header("Authorization", "Bearer " + token);
                    }
                    HttpResponse<String> res = client.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());
                    if (res.statusCode() == 200) {
                        List<Ingrediente> ingList = mapper.readValue(res.body(), new TypeReference<List<Ingrediente>>() {});
                        p.setIngredienti(ingList);
                    } else {
                        p.setIngredienti(new ArrayList<>());
                    }
                } catch (Exception ex) {
                    p.setIngredienti(new ArrayList<>());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


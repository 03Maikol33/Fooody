/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.resources;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.maikol.fooody.dao.ProdottoDAO;
import it.maikol.fooody.models.Caratteristica;
import it.maikol.fooody.models.Ingrediente;
import it.maikol.fooody.models.Prodotto;
import it.maikol.fooody.security.JwtAuthFilter;
import it.maikol.fooody.security.Secured;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.security.Key;
import java.util.List;
/**
 *
 * @author Maikol
 */
@Path("/prodotti")
public class ProdottoResource {
    /**
     * Lettura menu e ricerca filtrata.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMenu(
            @QueryParam("categoria") String categoria,
            @QueryParam("nome") String nome,
            @QueryParam("prezzoMin") Double prezzoMin,
            @QueryParam("prezzoMax") Double prezzoMax) {
        List<Prodotto> prodotti = ProdottoDAO.getMenu(categoria, nome, prezzoMin, prezzoMax);
        
        return Response.ok(prodotti).build();
    }

    /**
     * Eliminazione di una caratteristica
     */
    @DELETE
    @Secured //indica che ho bisogno di un token valido per poterlo fare
    @Path("/{idProdotto}/caratteristiche/{idCaratteristica}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminaCaratteristica(
            @PathParam("idProdotto") int idProdotto,
            @PathParam("idCaratteristica") int idCaratteristica) {
        
        boolean successo = ProdottoDAO.eliminaCaratteristicaDaProdotto(idProdotto, idCaratteristica);
        
        if (successo) {
            return Response.noContent().build(); //204 no content
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"errore\": \"Prodotto o caratteristica non trovati\"}")
                           .build();
        }
    }
    
    @GET
    @Path("/{idProdotto}/ingredienti")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIngredienti(
            @PathParam("idProdotto") int idProdotto,
            @Context HttpHeaders headers) {
        
        List<Ingrediente> ingredienti = ProdottoDAO.getIngredientiProdotto(idProdotto);
        
        if (ingredienti.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"errore\": \"Nessun ingrediente trovato per questo prodotto\"}")
                           .build();
        }
        boolean mostraQuantita = false;

        //etrazione  token
        String authHeader = headers.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer".length()).trim();
            try {
                var stringaSegreta = JwtAuthFilter.getKey();
                Key key = Keys.hmacShaKeyFor(stringaSegreta.getBytes());
                

                io.jsonwebtoken.Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                String ruolo = claims.get("ruolo", String.class);
                if ("personale".equals(ruolo) || "proprietario".equals(ruolo)) {
                    mostraQuantita = true;
                }
            } catch (Exception e) {
            }
        }

        //data masking
        if (!mostraQuantita) {
            for (Ingrediente ing : ingredienti) {
                ing.setQuantita(null);
            }
        }
        
        return Response.ok(ingredienti).build();
    }
    
    /**
     * endpoint per l'inserimento di un nuovo prodotto nel menu. Solo il proprietario può farlo.
     * @param p
     * @param context
     * @return 
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    public Response aggiungiProdotto(Prodotto p, @Context ContainerRequestContext context) {
        String ruolo = (String) context.getProperty("utenteRuolo");
        if (!"proprietario".equals(ruolo)) {
            return Response.status(Response.Status.FORBIDDEN).entity("{\"errore\": \"Accesso negato\"}").build();
        }

        if (ProdottoDAO.inserisciProdotto(p)) {
            return Response.status(Response.Status.CREATED).entity("{\"messaggio\": \"Prodotto aggiunto\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * endpoint per l'aggiornamento di un prodotto nel nenu. Solo il proprietario può farlo.
     * @param id
     * @param p
     * @param context
     * @return 
     */
    @PUT
    @Secured
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response aggiornaProdotto(@PathParam("id") int id, Prodotto p, @Context ContainerRequestContext context) {
        String ruolo = (String) context.getProperty("utenteRuolo");
        if (!"proprietario".equals(ruolo)) {
            return Response.status(Response.Status.FORBIDDEN).entity("{\"errore\": \"Accesso negato\"}").build();
        }

        p.setIdProdotto(id);
        if (ProdottoDAO.aggiornaProdotto(p)) {
            return Response.ok("{\"messaggio\": \"Prodotto aggiornato\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    
    /**
     * Associa una caratteristica esistente a un prodotto.
     */
    @POST
    @Secured
    @Path("/{idProdotto}/caratteristiche")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assegnaCaratteristica(
            @PathParam("idProdotto") int idProdotto, 
            Caratteristica caratteristica, 
            @Context ContainerRequestContext context) {
        
        String ruolo = (String) context.getProperty("utenteRuolo");
        if (!"proprietario".equals(ruolo)) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity("{\"errore\": \"Accesso negato. Solo il proprietario può modificare il menu.\"}")
                           .build();
        }

        if (caratteristica.getIdCaratteristica() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"errore\": \"Specificare un idCaratteristica valido.\"}")
                           .build();
        }

        boolean successo = ProdottoDAO.assegnaCaratteristicaAProdotto(idProdotto, caratteristica.getIdCaratteristica());
        
        if (successo) {
            return Response.status(Response.Status.CREATED)
                           .entity("{\"messaggio\": \"Caratteristica assegnata al prodotto con successo.\"}")
                           .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"errore\": \"Errore durante l'assegnazione. Verifica che la caratteristica esista e non sia già assegnata.\"}")
                           .build();
        }
    }
    
    /**
     * Restituisce la lista di tutte le categorie uniche presenti nel menu.
     */
    @GET
    @Path("/categorie")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategorie() {
        List<String> categorie = ProdottoDAO.getTutteLeCategorie();
        return Response.ok(categorie).build();
    }
    
    /**
     * Associazione di un ingrediente esistente ad un prodotto
     * @param idProdotto
     * @param ingrediente
     * @param context
     * @return 
     */
    @POST
    @Secured
    @Path("/{idProdotto}/ingredienti")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response aggiungiIngredienteAProdotto(
            @PathParam("idProdotto") int idProdotto, 
            Ingrediente ingrediente, 
            @Context ContainerRequestContext context) {
        
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        boolean successo = ProdottoDAO.associaIngredienteAProdotto(idProdotto, ingrediente.getIdIngrediente(), ingrediente.getQuantita());
        if (successo) {
            return Response.status(Response.Status.CREATED).entity("{\"messaggio\": \"Ingrediente associato con successo alla ricetta.\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Dissociazione di un ingrediente da un prodotto
     * @param idProdotto
     * @param idIngrediente
     * @param context
     * @return 
     */
    @DELETE
    @Secured
    @Path("/{idProdotto}/ingredienti/{idIngrediente}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response rimuoviIngredienteDaProdotto(
            @PathParam("idProdotto") int idProdotto, 
            @PathParam("idIngrediente") int idIngrediente, 
            @Context ContainerRequestContext context) {
        
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        boolean successo = ProdottoDAO.rimuoviIngredienteDaProdotto(idProdotto, idIngrediente);
        if (successo) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}

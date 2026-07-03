/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.resources;
import it.maikol.fooody.dao.OrdineDAO;
import it.maikol.fooody.dao.UtenteDAO;
import it.maikol.fooody.models.Ordine;
import it.maikol.fooody.models.Utente;
import it.maikol.fooody.security.Secured;
import jakarta.ws.rs.Consumes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
/**
 *
 * @author Maikol
 */
@Path("/clienti")
public class ClienteResource {
    /**
     * Ottieni lo storico degli ordini di un cliente specifico.
     */
    @GET
    @Secured
    @Path("/{idUtente}/ordini")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdiniCliente(
            @PathParam("idUtente") int idUtente,
            @Context ContainerRequestContext context) {
        
        //determina l'utente dal token
        String idLoggato = (String) context.getProperty("utenteId");
        String ruolo = (String) context.getProperty("utenteRuolo");
        

        if ("cliente".equals(ruolo) && !idLoggato.equals(String.valueOf(idUtente))) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity("{\"errore\": \"Violazione di sicurezza: non puoi visualizzare lo storico di un altro utente.\"}")
                           .build();
        }
        
        List<Ordine> ordini = OrdineDAO.getOrdiniByCliente(idUtente);
        return Response.ok(ordini).build();
    }
    
    
    /**
     * Aggiorna le informazioni del profilo di un cliente specifico.
     */
    @PUT
    @Secured
    @Path("/{idUtente}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response aggiornaProfilo(@PathParam("idUtente") int idUtente, Utente datiAggiornati, @Context ContainerRequestContext context) {
        
        int idLoggato = Integer.parseInt((String) context.getProperty("utenteId"));
        if (idLoggato != idUtente) {
            return Response.status(Response.Status.FORBIDDEN).entity("{\"errore\": \"Non puoi modificare il profilo di altri.\"}").build();
        }

        datiAggiornati.setIdUtente(idUtente);
        boolean successo = UtenteDAO.aggiornaProfilo(datiAggiornati);

        if (successo) {
            return Response.ok("{\"messaggio\": \"Profilo aggiornato con successo\"}").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"errore\": \"Errore durante l'aggiornamento\"}").build();
        }
    }
}

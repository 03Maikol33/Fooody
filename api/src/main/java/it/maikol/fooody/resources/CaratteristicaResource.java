/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.resources;
import it.maikol.fooody.dao.CaratteristicaDAO;
import it.maikol.fooody.models.Caratteristica;
import it.maikol.fooody.models.GruppoMutuaEsclusione;
import it.maikol.fooody.security.Secured;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
/**
 *
 * @author Maikol
 */
@Path("/caratteristiche")
public class CaratteristicaResource {
    @POST
    @Path("/gruppi")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response aggiungiGruppo(GruppoMutuaEsclusione gruppo, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (CaratteristicaDAO.inserisciGruppo(gruppo)) {
            return Response.status(Response.Status.CREATED).entity("{\"messaggio\": \"Gruppo creato con successo\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response aggiungiCaratteristica(Caratteristica c, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (CaratteristicaDAO.inserisciCaratteristica(c)) {
            return Response.status(Response.Status.CREATED).entity("{\"messaggio\": \"Caratteristica creata con successo\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    /**
     * ottieni tutti i gruppi di mutua esclusione presenti
     * @return 
     */
    @GET
    @Path("/gruppi")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGruppi() {
        return Response.ok(CaratteristicaDAO.getTuttiIGruppi()).build();
    }

    /**
     * modifica un gruppo di mutua esclusione
     * @param idGmc
     * @param gruppo
     * @param context
     * @return 
     */
    @PUT
    @Secured
    @Path("/gruppi/{idGmc}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response aggiornaGruppo(@PathParam("idGmc") int idGmc, GruppoMutuaEsclusione gruppo, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        gruppo.setIdGmc(idGmc);
        if (CaratteristicaDAO.aggiornaGruppo(gruppo)) {
            return Response.ok("{\"messaggio\": \"Gruppo aggiornato\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * cancella un gruppo di mutua esclusione
     * @param idGmc
     * @param context
     * @return 
     */
    @DELETE
    @Secured
    @Path("/gruppi/{idGmc}")
    public Response eliminaGruppo(@PathParam("idGmc") int idGmc, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (CaratteristicaDAO.eliminaGruppo(idGmc)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.CONFLICT).entity("{\"errore\": \"Impossibile eliminare. Verifica che non ci siano caratteristiche associate.\"}").build();
    }

    /**
     * ottieni tutte le caratteristiche presenti
     * @return 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCaratteristiche() {
        return Response.ok(CaratteristicaDAO.getTutteLeCaratteristiche()).build();
    }

    /**
     * modifica una caratteristica
     * @param id
     * @param c
     * @param context
     * @return 
     */
    @PUT
    @Secured
    @Path("/{idCaratteristica}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response aggiornaCaratteristica(@PathParam("idCaratteristica") int id, Caratteristica c, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        c.setIdCaratteristica(id);
        if (CaratteristicaDAO.aggiornaCaratteristica(c)) {
            return Response.ok("{\"messaggio\": \"Caratteristica aggiornata\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    /**
     * elimina una caratteristica
     * @param id
     * @param context
     * @return 
     */
    @DELETE
    @Secured
    @Path("/{idCaratteristica}")
    public Response eliminaCaratteristica(@PathParam("idCaratteristica") int id, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (CaratteristicaDAO.eliminaCaratteristica(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.CONFLICT).entity("{\"errore\": \"Impossibile eliminare. La caratteristica potrebbe essere in uso in ordini o prodotti.\"}").build();
    }
}

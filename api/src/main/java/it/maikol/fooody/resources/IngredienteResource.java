/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.resources;
import it.maikol.fooody.dao.IngredienteDAO;
import it.maikol.fooody.models.Ingrediente;
import it.maikol.fooody.security.Secured;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
@Path("/ingredienti")
public class IngredienteResource {
    
    /**
     * Ottieni tutti gli ingredienti esistenti
     * @return 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIngredienti() {
        return Response.ok(IngredienteDAO.getTuttiIngredienti()).build();
    }

    /**
     * Crea un nuovo ingrediente
     * @param i
     * @param context
     * @return 
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response creaIngrediente(Ingrediente i, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (IngredienteDAO.inserisciIngrediente(i)) {
            return Response.status(Response.Status.CREATED).entity("{\"messaggio\": \"Ingrediente creato\"}").build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Cancella un ingrediente
     * @param id
     * @param context
     * @return 
     */
    @DELETE
    @Secured
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminaIngrediente(@PathParam("id") int id, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (IngredienteDAO.eliminaIngrediente(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.CONFLICT).entity("{\"errore\": \"Ingrediente in uso\"}").build();
    }
}

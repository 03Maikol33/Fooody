/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.resources;

import it.maikol.fooody.dao.PersonaleDAO;
import it.maikol.fooody.security.Secured;
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
@Path("/personale")
public class PersonaleResource {
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStaff(@Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return Response.ok(PersonaleDAO.getTuttoIlPersonale()).build();
    }

    @DELETE
    @Secured
    @Path("/{id}")
    public Response licenzia(@PathParam("id") int id, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if (PersonaleDAO.rimuoviPersonale(id)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    /**
     * POST: Aggiunge un utente esistente allo staff.
     * @param idUtente
     * @param context
     * @return 
     */
    @POST
    @Secured
    @Path("/{idUtente}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response promuoviAPersonale(@PathParam("idUtente") int idUtente, @Context ContainerRequestContext context) {
        if (!"proprietario".equals(context.getProperty("utenteRuolo"))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        if (PersonaleDAO.aggiungiPersonale(idUtente)) {
            return Response.status(Response.Status.CREATED).build();
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}

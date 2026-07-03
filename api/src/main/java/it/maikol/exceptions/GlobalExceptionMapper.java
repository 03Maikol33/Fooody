/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.exceptions;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
/**
 *
 * @author Maikol
 *
 * Intercetta qualsiasi eccezione (Throwable) non gestita nel codice
 * e restituisce un JSON pulito invece della classica pagina di errore Tomcat.
 */
@Provider //Jersey attiva il filtro automaticamente
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        System.err.println("ERRORE NON GESTITO INTERCETTATO");
        exception.printStackTrace();

        //ottengo il messaggio dell'error
        String messaggioErrore = exception.getMessage() != null 
                                 ? exception.getMessage().replace("\"", "\\\"") 
                                 : "Errore sconosciuto";
        String jsonResponse = "{" +
                              "\"errore\": \"Si è verificato un errore interno sul server.\", " +
                              "\"dettaglio\": \"" + messaggioErrore + "\"" +
                              "}";

        // ritorna 500 internal server error
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .type(MediaType.APPLICATION_JSON)
                       .entity(jsonResponse)
                       .build();
    }
}

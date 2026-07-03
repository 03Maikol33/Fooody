/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.resources;
import it.maikol.fooody.dao.UtenteDAO;
import it.maikol.fooody.models.LoginRequest;
import it.maikol.fooody.models.LoginResponse;
import it.maikol.fooody.models.Utente;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

import io.jsonwebtoken.security.Keys;

import java.security.Key;
/**
 * 
 *
 * @author Maikol
 */
@Path("/auth")
public class AuthResource {

    //login
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) {
        
        Utente utente = UtenteDAO.login(request.getEmail(), request.getPassword());

        if (utente == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                           .entity("{\"errore\": \"Credenziali non valide\"}")
                           .build();
        }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUtente(utente);
        
        //token
        String stringaSegreta = "ChiaveChiaveEvaihcEvaihc33442211"; 
        Key key = Keys.hmacShaKeyFor(stringaSegreta.getBytes());

        String token = io.jsonwebtoken.Jwts.builder()
                .setSubject(String.valueOf(utente.getIdUtente()))
                .claim("ruolo", utente.getRuolo())                // aggiunge il ruolo nel payload
                .signWith(key)                                    // firma con la chiave 
                .compact();                                       // inserisce tutto nella stringa finale
        loginResponse.setToken(token);

        return Response.ok(loginResponse).build();
    }
    
    //registrazione
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Utente nuovoUtente) {
        
        if (nuovoUtente.getEmail() == null || nuovoUtente.getPassword() == null || nuovoUtente.getNome() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"errore\": \"Dati obbligatori mancanti\"}")
                           .build();
        }

        boolean creato = UtenteDAO.registraUtente(nuovoUtente);

        if (creato) {
            return Response.status(Response.Status.CREATED) // 201 Created
                           .entity("{\"messaggio\": \"Registrazione completata con successo\"}")
                           .build();
        } else {
            return Response.status(Response.Status.CONFLICT) // 409 Conflict
                           .entity("{\"errore\": \"Impossibile creare l'utente. L'email potrebbe essere già in uso.\"}")
                           .build();
        }
    }
}

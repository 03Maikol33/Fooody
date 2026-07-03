/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.security.Key;
/**
 *
 * @author Maikol
 */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {

    private static final String SECRET_KEY = "ChiaveChiaveEvaihcEvaihc33442211";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        
        //header Authorization
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"errore\": \"Accesso negato. Token mancante o formato non valido.\"}")
                        .build()
            );
            return;
        }

        //token
        String token = authorizationHeader.substring("Bearer".length()).trim();

        try {
            Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
            io.jsonwebtoken.Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            //scrive id e token nella richiesta
            requestContext.setProperty("utenteId", claims.getSubject());
            requestContext.setProperty("utenteRuolo", claims.get("ruolo", String.class));
        } catch (Exception e) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"errore\": \"Accesso negato. Token non valido.\"}")
                        .build()
            );
        }
    }
    
    public static String getKey(){
        return SECRET_KEY;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.security;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
/**
 *
 * @author Maikol
 * Filtro globale per abilitare il CORS
 * Aggiunge automaticamente gli header necessari a ogni risposta del server.
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext, 
                       ContainerResponseContext responseContext) throws IOException {
        
        //tutti i domini possono leggere
        responseContext.getHeaders().add(
            "Access-Control-Allow-Origin", "*");
            
        //si possono mandare le credenziali
        responseContext.getHeaders().add(
            "Access-Control-Allow-Credentials", "true");
            
        //specifica tutti gli header che possono essere allegati alle richieste
        responseContext.getHeaders().add(
            "Access-Control-Allow-Headers",
            "origin, content-type, accept, authorization");
            
        //specifica quali metodi http possono essere chiamati dall'applicativo esterno
        responseContext.getHeaders().add(
            "Access-Control-Allow-Methods", 
            "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}

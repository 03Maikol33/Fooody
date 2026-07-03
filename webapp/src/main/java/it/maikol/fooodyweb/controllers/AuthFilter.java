/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooodyweb.controllers;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
/**
 *
 * @author Maikol
 */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean isLoggedIn = (session != null && session.getAttribute("utente") != null);
        
        //Pagine che non richiedono autenticazione
        String path = httpRequest.getRequestURI();
        boolean isLoginPage = path.endsWith("/login") || path.endsWith("/register") || path.endsWith("/index") || path.endsWith("/");

        if (isLoggedIn || isLoginPage) {
            //L'utente passa
            chain.doFilter(request, response);
        } else {
            // L'utente viene reindirizzato alla pagina di login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }
}

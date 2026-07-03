/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooodyweb.models;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 *
 * @author Maikol
 */
public class AuthResponse {
    private String token;
    private Utente utente;

    @JsonAlias({"idCliente", "id_cliente", "clienteId"})
    private Integer idCliente;

    public AuthResponse() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) {
        this.utente = utente;
        if (this.utente != null && this.idCliente != null && this.utente.getIdCliente() == null) {
            this.utente.setIdCliente(this.idCliente);
        }
    }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
        if (this.utente != null && idCliente != null && this.utente.getIdCliente() == null) {
            this.utente.setIdCliente(idCliente);
        }
    }

    public String getRuolo() {
        if (this.utente != null) {
            return this.utente.getRuolo();
        }
        return null;
    }
}

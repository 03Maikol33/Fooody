/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.models;

/**
 *
 * @author Maikol
 */
public class Utente {
    private int idUtente;
    private String email;
    private String password;
    private String nome;
    private String cognome;
    private String ruolo;
    
    //dati relativi al cliente
    private String telefono;
    private String via;
    private String civico;
    private String citta;

    public Utente() {} // Costruttore vuoto necessario per JSON

    // Getters e Setters
    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
    
    //getters e setters del cliente
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }
    public String getCivico() { return civico; }
    public void setCivico(String civico) { this.civico = civico; }
    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }
}

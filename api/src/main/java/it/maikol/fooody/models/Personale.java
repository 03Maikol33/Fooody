/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.models;

/**
 *
 * @author Maikol
 */
public class Personale {
    private int idPersonale;
    private int idUtente;
    private String nome; //relativo a utente
    private String cognome; //relativo a utente

    public Personale() {}

    // Getters e Setters
    public int getIdPersonale() { return idPersonale; }
    public void setIdPersonale(int idPersonale) { this.idPersonale = idPersonale; }
    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
}

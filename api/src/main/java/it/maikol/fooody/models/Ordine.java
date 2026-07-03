/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.models;

/**
 *
 * @author Maikol
 */
public class Ordine {
    private int idOrdine;
    private String timeInserimento;
    private String orarioConsegnaRichiesto;
    private String statoCorrente;
    private double prezzoTotale;

    public Ordine() {}

    // Getters e Setters
    public int getIdOrdine() { return idOrdine; }
    public void setIdOrdine(int idOrdine) { this.idOrdine = idOrdine; }
    public String getTimeInserimento() { return timeInserimento; }
    public void setTimeInserimento(String timeInserimento) { this.timeInserimento = timeInserimento; }
    public String getOrarioConsegnaRichiesto() { return orarioConsegnaRichiesto; }
    public void setOrarioConsegnaRichiesto(String orarioConsegnaRichiesto) { this.orarioConsegnaRichiesto = orarioConsegnaRichiesto; }
    public String getStatoCorrente() { return statoCorrente; }
    public void setStatoCorrente(String statoCorrente) { this.statoCorrente = statoCorrente; }
    public double getPrezzoTotale() { return prezzoTotale; }
    public void setPrezzoTotale(double prezzoTotale) { this.prezzoTotale = prezzoTotale; }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.models;
import java.util.List;

/**
 *
 * @author Maikol
 */
public class DettaglioProdotto {
    private int idDettaglio;
    private String nomeProdotto;
    private int quantita;
    private List<String> caratteristiche; // Lista dei nomi delle caratteristiche
    private Integer tempoPreparazione; // Opzionale per il cliente
    private String procedura; // Opzionale per il cliente

    public DettaglioProdotto() {}

    // Getters e Setters
    public int getIdDettaglio() { return idDettaglio; }
    public void setIdDettaglio(int idDettaglio) { this.idDettaglio = idDettaglio; }
    public String getNomeProdotto() { return nomeProdotto; }
    public void setNomeProdotto(String nomeProdotto) { this.nomeProdotto = nomeProdotto; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
    public List<String> getCaratteristiche() { return caratteristiche; }
    public void setCaratteristiche(List<String> caratteristiche) { this.caratteristiche = caratteristiche; }
    public Integer getTempoPreparazione() { return tempoPreparazione; }
    public void setTempoPreparazione(Integer tempoPreparazione) { this.tempoPreparazione = tempoPreparazione; }
    public String getProcedura() { return procedura; }
    public void setProcedura(String procedura) { this.procedura = procedura; }
}

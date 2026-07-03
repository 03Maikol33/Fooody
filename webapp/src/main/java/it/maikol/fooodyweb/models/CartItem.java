package it.maikol.fooodyweb.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un elemento nel carrello dell'utente.
 * vive nella sessione HTTP.
 * Serializable per poter essere salvato in sessione.
 */
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idProdotto;
    private String nomeProdotto;
    private int quantita;
    private double prezzoUnitarioBase;
    private String immagine;
    private List<Integer> caratteristicheScelte;
    private List<String> nomiCaratteristicheScelte;
    private int tempoPreparazione;

    public CartItem() {
        this.caratteristicheScelte = new ArrayList<>();
        this.nomiCaratteristicheScelte = new ArrayList<>();
    }

    public CartItem(int idProdotto, String nomeProdotto, double prezzoUnitarioBase, String immagine, int quantita, List<Integer> caratteristicheScelte, List<String> nomiCaratteristicheScelte, int tempoPreparazione) {
        this.idProdotto = idProdotto;
        this.nomeProdotto = nomeProdotto;
        this.prezzoUnitarioBase = prezzoUnitarioBase;
        this.immagine = immagine;
        this.quantita = quantita;
        this.caratteristicheScelte = caratteristicheScelte != null ? caratteristicheScelte : new ArrayList<>();
        this.nomiCaratteristicheScelte = nomiCaratteristicheScelte != null ? nomiCaratteristicheScelte : new ArrayList<>();
        this.tempoPreparazione = tempoPreparazione;
    }

    // Getters e Setters
    public int getIdProdotto() { return idProdotto; }
    public void setIdProdotto(int idProdotto) { this.idProdotto = idProdotto; }

    public String getNomeProdotto() { return nomeProdotto; }
    public void setNomeProdotto(String nomeProdotto) { this.nomeProdotto = nomeProdotto; }

    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }

    public double getPrezzoUnitarioBase() { return prezzoUnitarioBase; }
    public void setPrezzoUnitarioBase(double prezzoUnitarioBase) { this.prezzoUnitarioBase = prezzoUnitarioBase; }

    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }

    public List<Integer> getCaratteristicheScelte() { return caratteristicheScelte; }
    public void setCaratteristicheScelte(List<Integer> caratteristicheScelte) { this.caratteristicheScelte = caratteristicheScelte; }

    public List<String> getNomiCaratteristicheScelte() { return nomiCaratteristicheScelte; }
    public void setNomiCaratteristicheScelte(List<String> nomiCaratteristicheScelte) { this.nomiCaratteristicheScelte = nomiCaratteristicheScelte; }

    public int getTempoPreparazione() { return tempoPreparazione; }
    public void setTempoPreparazione(int tempoPreparazione) { this.tempoPreparazione = tempoPreparazione; }

    /**
     * Calcola il subtotale di questo item (prezzo unitario comprensivo di variazioni × quantità).
     */
    public double getSubtotale() {
        return prezzoUnitarioBase * quantita;
    }
}

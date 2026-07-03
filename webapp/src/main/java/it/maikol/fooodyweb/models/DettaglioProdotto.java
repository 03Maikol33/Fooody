package it.maikol.fooodyweb.models;

import java.util.List;

/**
 * Modello che rappresenta un prodotto all'interno di un ordine.
 */
public class DettaglioProdotto {
    private int idDettaglio;
    private String nomeProdotto;
    private int quantita;
    private List<String> caratteristiche;
    private Integer tempoPreparazione;
    private String procedura;

    public DettaglioProdotto() {}

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

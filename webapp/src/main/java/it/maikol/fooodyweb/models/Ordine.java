package it.maikol.fooodyweb.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Modello che rappresenta un Ordine.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ordine {
    private int idOrdine;

    @JsonAlias({"idCliente", "idUtente"})
    private int idCliente;

    private String timeInserimento;

    @JsonAlias({"orarioConsegnaRichiesto", "OrarioConsegnaRichiesto"})
    private String orarioConsegnaRichiesto;

    private String statoCorrente;
    private double prezzoTotale;
    private List<DettaglioProdotto> dettagli;
    private List<Map<String, Object>> operatori;
    private List<Map<String, Object>> storicoStati;

    public Ordine() {}

    public List<Map<String, Object>> getOperatori() { return operatori; }
    public void setOperatori(List<Map<String, Object>> operatori) { this.operatori = operatori; }

    public List<Map<String, Object>> getStoricoStati() { return storicoStati; }
    public void setStoricoStati(List<Map<String, Object>> storicoStati) { this.storicoStati = storicoStati; }

    public List<DettaglioProdotto> getDettagli() { return dettagli; }
    public void setDettagli(List<DettaglioProdotto> dettagli) { this.dettagli = dettagli; }

    public int getIdOrdine() { return idOrdine; }
    public void setIdOrdine(int idOrdine) { this.idOrdine = idOrdine; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getTimeInserimento() { return timeInserimento; }
    public void setTimeInserimento(String timeInserimento) { this.timeInserimento = timeInserimento; }

    public String getOrarioConsegnaRichiesto() { return orarioConsegnaRichiesto; }
    public void setOrarioConsegnaRichiesto(String orarioConsegnaRichiesto) { this.orarioConsegnaRichiesto = orarioConsegnaRichiesto; }

    public String getStatoCorrente() { return statoCorrente; }
    public void setStatoCorrente(String statoCorrente) { this.statoCorrente = statoCorrente; }

    public double getPrezzoTotale() { return prezzoTotale; }
    public void setPrezzoTotale(double prezzoTotale) { this.prezzoTotale = prezzoTotale; }

    /**
     * Restituisce il colore CSS associato allo stato dell'ordine.
     */
    public String getStatoColore() {
        if (statoCorrente == null) return "#999999";
        return switch (statoCorrente) {
            case "inserito"                            -> "#e67e22";
            case "in preparazione", "in_preparazione"  -> "#f1c40f";
            case "pronto"                              -> "#3498db";
            case "in consegna", "in_consegna"          -> "#9b59b6";
            case "consegnato"                          -> "#2ecc71";
            case "annullato"                           -> "#e74c3c";
            default                                    -> "#999999";
        };
    }

    /**
     * Restituisce l'etichetta leggibile dello stato.
     */
    public String getStatoLabel() {
        if (statoCorrente == null) return "Sconosciuto";
        return switch (statoCorrente) {
            case "inserito"                            -> "Inserito";
            case "in preparazione", "in_preparazione"  -> "In Preparazione";
            case "pronto"                              -> "Pronto";
            case "in consegna", "in_consegna"          -> "In Consegna";
            case "consegnato"                          -> "Consegnato";
            case "annullato"                           -> "Annullato";
            default                                    -> statoCorrente;
        };
    }
}

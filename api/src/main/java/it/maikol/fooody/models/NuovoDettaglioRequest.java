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
public class NuovoDettaglioRequest {
    private int idProdotto;
    private int quantita;
    private List<Integer> caratteristicheScelte; // Array di ID inviati dal frontend

    public NuovoDettaglioRequest() {}

    public int getIdProdotto() { return idProdotto; }
    public void setIdProdotto(int idProdotto) { this.idProdotto = idProdotto; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
    public List<Integer> getCaratteristicheScelte() { return caratteristicheScelte; }
    public void setCaratteristicheScelte(List<Integer> caratteristicheScelte) { this.caratteristicheScelte = caratteristicheScelte; }
}

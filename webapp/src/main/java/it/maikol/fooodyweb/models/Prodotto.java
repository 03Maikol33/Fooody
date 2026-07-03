/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooodyweb.models;

import java.util.List;

/**
 *
 * @author Maikol
 */
public class Prodotto {
    private int idProdotto;
    private String categoria;
    private String nome;
    private String descrizione;
    private double prezzoBase;
    private String immagine;
    private Integer tempoPreparazione;
    private String procedura;
    private List<Caratteristica> caratteristiche;
    private List<Ingrediente> ingredienti;

    public Prodotto() {}

    // Getters e Setters
    public int getIdProdotto() { return idProdotto; }
    public void setIdProdotto(int idProdotto) { this.idProdotto = idProdotto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public double getPrezzoBase() { return prezzoBase; }
    public void setPrezzoBase(double prezzoBase) { this.prezzoBase = prezzoBase; }
    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }
    public Integer getTempoPreparazione() { return tempoPreparazione; }
    public void setTempoPreparazione(Integer tempoPreparazione) { this.tempoPreparazione = tempoPreparazione; }
    public String getProcedura() { return procedura; }
    public void setProcedura(String procedura) { this.procedura = procedura; }
    public List<Caratteristica> getCaratteristiche() { return caratteristiche; }
    public void setCaratteristiche(List<Caratteristica> caratteristiche) { this.caratteristiche = caratteristiche; }
    public List<Ingrediente> getIngredienti() { return ingredienti; }
    public void setIngredienti(List<Ingrediente> ingredienti) { this.ingredienti = ingredienti; }
}


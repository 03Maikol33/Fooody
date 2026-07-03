/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooodyweb.models;

/**
 * Modello per la rappresentazione degli Ingredienti nel client Web.
 * @author Maikol
 */
public class Ingrediente {
    private int idIngrediente;
    private String nome;
    private String quantita;

    public Ingrediente() {}

    public int getIdIngrediente() { return idIngrediente; }
    public void setIdIngrediente(int idIngrediente) { this.idIngrediente = idIngrediente; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getQuantita() { return quantita; }
    public void setQuantita(String quantita) { this.quantita = quantita; }
}

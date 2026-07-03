/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.models;

/**
 *
 * @author Maikol
 */
public class Caratteristica {
    private int idCaratteristica;
    private String nome;
    private double differenzaPrezzo;
    private boolean isDefault;
    private String descrizione;
    private Integer idGmc;

    public Caratteristica() {}

    // Getters e Setters
    public int getIdCaratteristica() { return idCaratteristica; }
    public void setIdCaratteristica(int idCaratteristica) { this.idCaratteristica = idCaratteristica; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescrizione(){return descrizione;}
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public double getDifferenzaPrezzo() { return differenzaPrezzo; }
    public void setDifferenzaPrezzo(double differenzaPrezzo) { this.differenzaPrezzo = differenzaPrezzo; }
    public boolean getIsDefault() { return isDefault; }
    public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }
    public Integer getidGmc() { return idGmc; }
    public void setidGmc(Integer idGmc) { this.idGmc = idGmc; }
    public boolean isDefault(){return isDefault;}
}
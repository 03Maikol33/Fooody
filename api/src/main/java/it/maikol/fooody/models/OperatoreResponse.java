/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.models;

/**
 *
 * @author Maikol
 */
public class OperatoreResponse {
    private String nome;
    private String cognome;

    public OperatoreResponse() {}
    public String getNome() { 
        return nome; 
    }
    
    public void setNome(String nome) { 
        this.nome = nome; 
    }

    public String getCognome() { 
        return cognome; 
    }

    public void setCognome(String cognome) { 
        this.cognome = cognome; 
    }
}

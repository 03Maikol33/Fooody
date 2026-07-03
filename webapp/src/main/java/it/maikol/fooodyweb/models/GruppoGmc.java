package it.maikol.fooodyweb.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GruppoGmc {
    private int idGmc;
    private String nome;
    private String descrizione;

    public GruppoGmc() {}

    public int getIdGmc() { return idGmc; }
    public void setIdGmc(int idGmc) { this.idGmc = idGmc; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}

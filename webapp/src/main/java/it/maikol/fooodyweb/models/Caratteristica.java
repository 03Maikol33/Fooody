package it.maikol.fooodyweb.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Caratteristica {
    private int idCaratteristica;
    private String nome;
    private double differenzaPrezzo;
    private boolean isDefault;
    private String gruppo;
    private Integer idGmc;
    private String descrizione;

    public Caratteristica() {}

    public int getIdCaratteristica() { return idCaratteristica; }
    public void setIdCaratteristica(int idCaratteristica) { this.idCaratteristica = idCaratteristica; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getDifferenzaPrezzo() { return differenzaPrezzo; }
    public void setDifferenzaPrezzo(double differenzaPrezzo) { this.differenzaPrezzo = differenzaPrezzo; }

    public boolean isIsDefault() { return isDefault; }
    public void setIsDefault(boolean isDefault) { this.isDefault = isDefault; }

    public String getGruppo() {
        if (gruppo != null) return gruppo;
        if (idGmc != null && idGmc > 0) return (idGmc == 1 ? "Zucchero" : "Gruppo " + idGmc);
        return null;
    }
    public void setGruppo(String gruppo) { this.gruppo = gruppo; }

    public Integer getIdGmc() { return idGmc; }
    public void setIdGmc(Integer idGmc) { this.idGmc = idGmc; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
}

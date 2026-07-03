package it.maikol.fooodyweb.models;

/**
 * Modello che rappresenta un membro del personale.
 */
public class Personale {
    private int idPersonale;
    private int idUtente;
    private String nome;
    private String cognome;

    public Personale() {}

    public Personale(int idPersonale, int idUtente, String nome, String cognome) {
        this.idPersonale = idPersonale;
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
    }

    public int getIdPersonale() { return idPersonale; }
    public void setIdPersonale(int idPersonale) { this.idPersonale = idPersonale; }

    public int getIdUtente() { return idUtente; }
    public void setIdUtente(int idUtente) { this.idUtente = idUtente; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
}

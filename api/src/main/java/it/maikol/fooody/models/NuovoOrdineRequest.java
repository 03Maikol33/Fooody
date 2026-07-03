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
public class NuovoOrdineRequest {
    private int idCliente;
    private String orarioConsegnaRichiesto;
    private List<NuovoDettaglioRequest> items;

    public NuovoOrdineRequest() {}

    // getters e Setters
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public String getOrarioConsegnaRichiesto() { return orarioConsegnaRichiesto; }
    public void setOrarioConsegnaRichiesto(String orarioConsegnaRichiesto) { this.orarioConsegnaRichiesto = orarioConsegnaRichiesto; }
    public List<NuovoDettaglioRequest> getItems() { return items; }
    public void setItems(List<NuovoDettaglioRequest> items) { this.items = items; }
}

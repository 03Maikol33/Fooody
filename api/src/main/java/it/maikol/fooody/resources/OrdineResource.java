/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.resources;
import it.maikol.fooody.dao.OrdineDAO;
import it.maikol.fooody.models.DettaglioProdotto;
import it.maikol.fooody.models.NuovoDettaglioRequest;
import it.maikol.fooody.models.NuovoOrdineRequest;
import it.maikol.fooody.models.OperatoreResponse;
import it.maikol.fooody.models.Ordine;
import it.maikol.fooody.models.StatoRequest;
import it.maikol.fooody.security.Secured;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
/**
 *
 * @author Maikol
 */
@Path("/ordini")
public class OrdineResource {
   /**
     * Aggiungi un prodotto a un ordine esistente. Questo endpoint consente di aggiungere un nuovo prodotto a un ordine specifico, insieme alle caratteristiche selezionate.
     */
    @POST
    @Secured
    @Path("/{idOrdine}/dettagli")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response aggiungiProdotto(
            @PathParam("idOrdine") int idOrdine,
            NuovoDettaglioRequest request) {
        
        boolean successo = OrdineDAO.aggiungiProdottoAOrdine(idOrdine, request);
        
        if (successo) {
            // 201 Created
            return Response.status(Response.Status.CREATED)
                           .entity("{\"messaggio\": \"Prodotto aggiunto all'ordine con successo\"}")
                           .build();
        } else {
            // 500 Internal Server Error
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"errore\": \"Impossibile aggiungere il prodotto all'ordine. Controlla che l'ordine esista.\"}")
                           .build();
        }
    }
    
    /**
     * Ottieni il tempo stimato per la preparazione di un ordine specifico.
     */
    @GET
    @Secured
    @Path("/{idOrdine}/tempo-stimato")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTempoStimato(@PathParam("idOrdine") int idOrdine) {
        
        int tempoMinuti = OrdineDAO.calcolaTempoStimato(idOrdine);
        String jsonRisposta = "{" +
                              "\"tempoStimatoMinuti\": " + tempoMinuti + ", " +
                              "\"messaggio\": \"Il tempo stimato per la preparazione è di " + tempoMinuti + " minuti.\"" +
                              "}";
        
        return Response.ok(jsonRisposta).build();
    }

    /**
     * Ottieni il prezzo totale di un ordine specifico. Questo endpoint calcola il prezzo totale dell'ordine, considerando i prodotti e le loro caratteristiche selezionate.
     */
    @GET
    @Secured
    @Path("/{idOrdine}/prezzo-totale")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrezzoTotale(@PathParam("idOrdine") int idOrdine) {
        
        double prezzo = OrdineDAO.calcolaPrezzoTotale(idOrdine);
        String jsonRisposta = "{" +
                              "\"prezzoTotale\": " + prezzo + ", " +
                              "\"messaggio\": \"Il prezzo totale dell'ordine è di " + prezzo + " euro.\"" +
                              "}";
        
        return Response.ok(jsonRisposta).build();
    }
    
    /**
     * Cmbia lo stato di un ordine specifico. Questo endpoint consente di aggiornare lo stato di un ordine, ad esempio da "in preparazione" a "pronto" o "in consegna".
     */
    @POST
    @Secured
    @Path("/{idOrdine}/stati")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiaStatoOrdine(
            @PathParam("idOrdine") int idOrdine, 
            StatoRequest request,
            @Context ContainerRequestContext context) { 
        
        String ruoloUtente = (String) context.getProperty("utenteRuolo");
        if (!"personale".equals(ruoloUtente) && !"proprietario".equals(ruoloUtente)) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity("{\"errore\": \"Azione non consentita: permessi insufficienti.\"}")
                           .build();
        }
        
        int idLoggato = Integer.parseInt((String) context.getProperty("utenteId"));

        //stato attuale ordine
        String statoAttuale = OrdineDAO.getStatoCorrente(idOrdine);
        if (statoAttuale == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"errore\": \"Ordine non trovato.\"}")
                           .build();
        }
        String nuovoStato = request.getNuovoStato();
        if (!isTransizioneValida(statoAttuale, nuovoStato)) {
            // Errore 400 Bad Request
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"errore\": \"Transizione di stato non valida da '" + statoAttuale + "' a '" + nuovoStato + "'.\"}")
                           .build();
        }
        boolean successo = OrdineDAO.cambiaStatoOrdine(idOrdine, nuovoStato, idLoggato, ruoloUtente);
        
        if (successo) {
            return Response.ok("{\"messaggio\": \"Stato ordine aggiornato con successo a " + nuovoStato + "\"}").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"errore\": \"Errore durante l'aggiornamento.\"}")
                           .build();
        }
    }

    /**
     * Metodo di supporto per verificare se la transizione di stato è valida secondo le regole definite.
     */
    private boolean isTransizioneValida(String statoCorrente, String nuovoStato) {

        if ("annullato".equals(nuovoStato)) {
            return !"in consegna".equals(statoCorrente) && !"pronto".equals(statoCorrente) && !"consegnato".equals(statoCorrente) && !"annullato".equals(statoCorrente);
        }

        switch (statoCorrente) {
            case "inserito":
                return "in preparazione".equals(nuovoStato);
            case "in preparazione":
                return "pronto".equals(nuovoStato);
            case "pronto":
                return "in consegna".equals(nuovoStato);
            case "in consegna":
                return "consegnato".equals(nuovoStato);
            case "consegnato":
            case "annullato":
                return false;
            default:
                return false;
        }
    }
    
    /**
     * Ottieni l'elenco di tutti gli ordini, con la possibilità di filtrare per data di inserimento e stato. Questo endpoint è accessibile solo al personale e al proprietario.
     */
    @GET
    @Secured
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdini(
            @QueryParam("dataInserimento") String dataInserimento,
            @QueryParam("stato") String stato,
            @Context ContainerRequestContext context) {
       
        String ruoloUtente = (String) context.getProperty("utenteRuolo");
        if (!"personale".equals(ruoloUtente) && !"proprietario".equals(ruoloUtente)) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity("{\"errore\": \"Accesso negato. Solo il personale può visualizzare l'elenco ordini globale.\"}")
                           .build();
        }
        
        List<Ordine> ordini = OrdineDAO.getOrdiniFiltrati(dataInserimento, stato);
        return Response.ok(ordini).build();
    }
    
    /**
     * Annulla un ordine specifico. Questo endpoint consente di annullare un ordine, se le condizioni lo permettono (ad esempio, non può essere annullato se è già in consegna o consegnato). Solo il cliente proprietario dell'ordine o il personale possono annullarlo.
     */
    @DELETE
    @Secured
    @Path("/{idOrdine}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response annullaOrdine(
            @PathParam("idOrdine") int idOrdine,
            @Context ContainerRequestContext context) {
        //info da token
        String ruoloUtente = (String) context.getProperty("utenteRuolo");
        String idLoggatoString = (String) context.getProperty("utenteId");
        int idLoggato = Integer.parseInt(idLoggatoString);

        Integer idProprietarioOrdine = OrdineDAO.getIdUtenteProprietarioOrdine(idOrdine);
        if (idProprietarioOrdine == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"errore\": \"Ordine non trovato.\"}")
                           .build();
        }

        if ("cliente".equals(ruoloUtente) && idProprietarioOrdine != idLoggato) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity("{\"errore\": \"Non sei autorizzato ad annullare l'ordine di un altro cliente.\"}")
                           .build();
        }

        String statoAttuale = OrdineDAO.getStatoCorrente(idOrdine);
        if (!isTransizioneValida(statoAttuale, "annullato")) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"errore\": \"Impossibile annullare un ordine che si trova nello stato: " + statoAttuale + "\"}")
                           .build();
        }

        boolean successo = OrdineDAO.cambiaStatoOrdine(idOrdine, "annullato", idLoggato, ruoloUtente);
        
        if (successo) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"errore\": \"Errore durante l'annullamento.\"}")
                           .build();
        }
    }
    
    /**
     * Estrae la lista dei prodotti nel carrello, nasconde le ricette ai clienti.
     */
    @GET
    @Secured
    @Path("/{idOrdine}/dettagli")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDettagliOrdine(
            @PathParam("idOrdine") int idOrdine,
            @Context ContainerRequestContext context) {

        String ruoloUtente = (String) context.getProperty("utenteRuolo");
        int idLoggato = Integer.parseInt((String) context.getProperty("utenteId"));

        Integer idProprietario = OrdineDAO.getIdUtenteProprietarioOrdine(idOrdine);
        if (idProprietario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"errore\": \"Ordine non trovato.\"}")
                           .build();
        }

        if ("cliente".equals(ruoloUtente) && idProprietario != idLoggato) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity("{\"errore\": \"Non sei autorizzato a visualizzare lo scontrino di un altro utente.\"}")
                           .build();
        }

        List<DettaglioProdotto> dettagli = OrdineDAO.getDettagliOrdine(idOrdine);

        if ("cliente".equals(ruoloUtente)) {
            for (DettaglioProdotto dp : dettagli) {
                dp.setProcedura(null);
            }
        }

        return Response.ok(dettagli).build();
    }
    
    /**
     * Restituisce il personale che ha gestito l'ordine.
     */
    @GET
    @Secured
    @Path("/{idOrdine}/operatori")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOperatoriOrdine(@PathParam("idOrdine") int idOrdine) {

        if (OrdineDAO.getStatoCorrente(idOrdine) == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("{\"errore\": \"Ordine non trovato.\"}")
                           .build();
        }
        
        List<OperatoreResponse> operatori = OrdineDAO.getOperatoriOrdine(idOrdine);
        
        return Response.ok(operatori).build();
    }
    
    
    /**
     *Creazione di un nuovo ordine completo dal carrello
     */
    @POST
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response creaNuovoOrdine(NuovoOrdineRequest request) {
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("{\"errore\": \"Impossibile creare un ordine vuoto.\"}")
                           .build();
        }

        Integer idGenerato = OrdineDAO.creaNuovoOrdine(request);

        if (idGenerato != null) {
            return Response.status(Response.Status.CREATED)
                           .entity("{\"messaggio\": \"Ordine creato con successo\", \"idOrdine\": " + idGenerato + "}")
                           .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity("{\"errore\": \"Errore interno del server durante la creazione dell'ordine.\"}")
                           .build();
        }
    }
}

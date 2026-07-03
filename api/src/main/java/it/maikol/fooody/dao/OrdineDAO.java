/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.dao;
import it.maikol.fooody.dao.DatabaseManager;
import it.maikol.fooody.models.DettaglioProdotto;
import it.maikol.fooody.models.NuovoDettaglioRequest;
import it.maikol.fooody.models.OperatoreResponse;
import it.maikol.fooody.models.Ordine;
import it.maikol.fooody.models.NuovoOrdineRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Maikol
 */
public class OrdineDAO {
    /**
     * Crea nuovo ordine. Ordine, Dettagli, Caratteristiche in ordine
     */
    public static Integer creaNuovoOrdine(NuovoOrdineRequest request) {
        String insertOrdine = "INSERT INTO Ordine (orarioConsegnaRichiesto, tempoStimato, prezzoTot, idCliente, statoCorrente) VALUES (?, 0, 0.0, ?, 'inserito')";
        String updateOrdineTotali = "UPDATE Ordine SET tempoStimato = ?, prezzoTot = ? WHERE idOrdine = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            //inizio transazione
            conn.setAutoCommit(false);

            //prende l'id cliente a partire da quello utente
            int veroIdCliente = -1;
            String queryCliente = "SELECT idCliente FROM Cliente WHERE idUtente = ?";
            try (PreparedStatement stmtC = conn.prepareStatement(queryCliente)) {
                stmtC.setInt(1, request.getIdCliente()); //contiene l'idUtente
                try (ResultSet rsC = stmtC.executeQuery()) {
                    if (rsC.next()) {
                        veroIdCliente = rsC.getInt("idCliente");
                    } else {
                        conn.rollback();
                        return null;
                    }
                }
            }
            
            int idOrdineGenerato = -1;
            //ordine vuoto
            try (PreparedStatement stmtOrdine = conn.prepareStatement(insertOrdine, Statement.RETURN_GENERATED_KEYS)) {
                stmtOrdine.setString(1, request.getOrarioConsegnaRichiesto());
                stmtOrdine.setInt(2, veroIdCliente);
                stmtOrdine.executeUpdate();

                try (ResultSet rs = stmtOrdine.getGeneratedKeys()) {
                    if (rs.next()) idOrdineGenerato = rs.getInt(1);
                    else { conn.rollback(); return null; }
                }
            }

            //inseriemento dettagli e caratteristiche
            for (NuovoDettaglioRequest item : request.getItems()) {
                aggiungiProdottoInternal(conn, idOrdineGenerato, item);
            }

            //calcolo prezzo e tempo
            int tempoTotale = calcolaTempoInternal(conn, idOrdineGenerato);
            double prezzoTotale = calcolaPrezzoInternal(conn, idOrdineGenerato);

            //aggiornamento dell'ordine
            try (PreparedStatement stmtUpdate = conn.prepareStatement(updateOrdineTotali)) {
                stmtUpdate.setInt(1, tempoTotale);
                stmtUpdate.setDouble(2, prezzoTotale);
                stmtUpdate.setInt(3, idOrdineGenerato);
                stmtUpdate.executeUpdate();
            }

            conn.commit();
            return idOrdineGenerato;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
    
    /**
     * Aggiunge un prodotto a un ordine esistente
     * @param conn
     * @param idOrdine
     * @param request
     * @throws SQLException
     */
    private static void aggiungiProdottoInternal(Connection conn, int idOrdine, NuovoDettaglioRequest request) throws SQLException {
        String insertDettaglio = "INSERT INTO DettaglioProdotto (quantita, idOrdine, idProdotto) VALUES (?, ?, ?)";
        String insertCaratteristica = "INSERT INTO AssociazioneCaratteristica (idDettaglio, idCaratteristica) VALUES (?, ?)";

        try (PreparedStatement stmtDettaglio = conn.prepareStatement(insertDettaglio, Statement.RETURN_GENERATED_KEYS)) {
            stmtDettaglio.setInt(1, request.getQuantita());
            stmtDettaglio.setInt(2, idOrdine);
            stmtDettaglio.setInt(3, request.getIdProdotto());
            stmtDettaglio.executeUpdate();

            try (ResultSet rsDett = stmtDettaglio.getGeneratedKeys()) {
                if (rsDett.next()) {
                    int idDettaglio = rsDett.getInt(1);
                    if (request.getCaratteristicheScelte() != null && !request.getCaratteristicheScelte().isEmpty()) {
                        try (PreparedStatement stmtCaratt = conn.prepareStatement(insertCaratteristica)) {
                            for (Integer idCarat : request.getCaratteristicheScelte()) {
                                stmtCaratt.setInt(1, idDettaglio);
                                stmtCaratt.setInt(2, idCarat);
                                stmtCaratt.addBatch();
                            }
                            stmtCaratt.executeBatch();
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Calcola il tempo stimato per un ordine
     * @param conn
     * @param idOrdine
     * @return
     * @throws SQLException
     */
    private static int calcolaTempoInternal(Connection conn, int idOrdine) throws SQLException {
        int tempo = 0;
        String query = "SELECT SUM(p.tempoPreparazione * dp.quantita) AS totale FROM DettaglioProdotto dp JOIN Prodotto p ON dp.idProdotto = p.idProdotto WHERE dp.idOrdine = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) tempo = rs.getInt("totale");
            }
        }
        return tempo;
    }

    /**
     * Calcola il prezzo totale per un ordine
     * @param conn
     * @param idOrdine
     * @return
     * @throws SQLException
     */
    private static double calcolaPrezzoInternal(Connection conn, int idOrdine) throws SQLException {
        double prezzo = 0.0;
        String query = "SELECT SUM((p.prezzoBase + COALESCE(c_sum.diffPrezzo, 0)) * dp.quantita) AS totale FROM DettaglioProdotto dp JOIN Prodotto p ON dp.idProdotto = p.idProdotto LEFT JOIN (SELECT ac.idDettaglio, SUM(c.differenzaPrezzo) AS diffPrezzo FROM AssociazioneCaratteristica ac JOIN Caratteristica c ON ac.idCaratteristica = c.idCaratteristica GROUP BY ac.idDettaglio) c_sum ON dp.idDettaglio = c_sum.idDettaglio WHERE dp.idOrdine = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) prezzo = rs.getDouble("totale");
            }
        }
        return prezzo;
    }
    
    
    /**
     * Inserimento di un prodotto in un ordine esistente, con caratteristiche opzionali. Tutto in una transazione.
     * @param idOrdine
     * @param request
     * @return true se l'inserimento è avvenuto con successo, false altrimenti
     */
    public static boolean aggiungiProdottoAOrdine(int idOrdine, NuovoDettaglioRequest request) {
        
        String insertDettaglio = "INSERT INTO DettaglioProdotto (quantita, idOrdine, idProdotto) VALUES (?, ?, ?)";
        String insertCaratteristica = "INSERT INTO AssociazioneCaratteristica (idDettaglio, idCaratteristica) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            
            //inizio transazione
            conn.setAutoCommit(false);

            try (PreparedStatement stmtDettaglio = conn.prepareStatement(insertDettaglio, Statement.RETURN_GENERATED_KEYS)) {
                
                stmtDettaglio.setInt(1, request.getQuantita());
                stmtDettaglio.setInt(2, idOrdine);
                stmtDettaglio.setInt(3, request.getIdProdotto());
                stmtDettaglio.executeUpdate();

                try (ResultSet generatedKeys = stmtDettaglio.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idDettaglioGenerato = generatedKeys.getInt(1);

                        // associa le caratteristiche a questo id
                        if (request.getCaratteristicheScelte() != null && !request.getCaratteristicheScelte().isEmpty()) {
                            try (PreparedStatement stmtCaratt = conn.prepareStatement(insertCaratteristica)) {
                                for (Integer idCaratteristica : request.getCaratteristicheScelte()) {
                                    stmtCaratt.setInt(1, idDettaglioGenerato);
                                    stmtCaratt.setInt(2, idCaratteristica);
                                    stmtCaratt.addBatch();
                                }
                                stmtCaratt.executeBatch();
                            }
                        }
                    }
                }
                
                //fine transazione
                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback(); 
                e.printStackTrace();
                return false;
                
            } finally {
                conn.setAutoCommit(true); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Calcolo del tempo stimato
     */
    public static int calcolaTempoStimato(int idOrdine) {
        int tempoTotale = 0;
        //calcolo delegato al database
        String query = "SELECT SUM(p.tempoPreparazione * dp.quantita) AS totale " +
                       "FROM DettaglioProdotto dp " +
                       "JOIN Prodotto p ON dp.idProdotto = p.idProdotto " +
                       "WHERE dp.idOrdine = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tempoTotale = rs.getInt("totale");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tempoTotale;
    }

    /**
     * Calcolo del prezzo totale
     */
    public static double calcolaPrezzoTotale(int idOrdine) {
        double prezzoTotale = 0.0;
        
        // la sotto-query somma prima il costo delle varianti
        // poi lo aggiunge al prezzo base del prodotto e moltiplica per la quantità
        String query = "SELECT SUM((p.prezzoBase + COALESCE(c_sum.diffPrezzo, 0)) * dp.quantita) AS totale " +
                       "FROM DettaglioProdotto dp " +
                       "JOIN Prodotto p ON dp.idProdotto = p.idProdotto " +
                       "LEFT JOIN (" +
                       "    SELECT ac.idDettaglio, SUM(c.differenzaPrezzo) AS diffPrezzo " +
                       "    FROM AssociazioneCaratteristica ac " +
                       "    JOIN Caratteristica c ON ac.idCaratteristica = c.idCaratteristica " +
                       "    GROUP BY ac.idDettaglio" +
                       ") c_sum ON dp.idDettaglio = c_sum.idDettaglio " +
                       "WHERE dp.idOrdine = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    prezzoTotale = rs.getDouble("totale");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prezzoTotale;
    }
    
    /**
     * Aggiornamento dello stato di un ordine con sritture  nello storico
     */
    public static boolean cambiaStatoOrdine(int idOrdine, String nuovoStato, int idUtenteLoggato, String ruoloUtente) {
        String updateOrdine = "UPDATE Ordine SET statoCorrente = ? WHERE idOrdine = ?";
        String insertCambio = "INSERT INTO CambioStato (stato, idOrdine, idPersonale) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false); 
            
            try (PreparedStatement stmtUpdate = conn.prepareStatement(updateOrdine);
                 PreparedStatement stmtInsert = conn.prepareStatement(insertCambio)) {
                
                //aggiorno ordine
                stmtUpdate.setString(1, nuovoStato);
                stmtUpdate.setInt(2, idOrdine);
                int righeModificate = stmtUpdate.executeUpdate();
                if (righeModificate == 0) {
                    conn.rollback();
                    return false;
                }
                
                //ottiene l'id del personale
                Integer idPersonale = null;
                if ("personale".equals(ruoloUtente) || "proprietario".equals(ruoloUtente)) {
                    idPersonale = getIdPersonaleDaUtente(conn, idUtenteLoggato);
                }
                
                //inserisce il cambio di stato
                stmtInsert.setString(1, nuovoStato);
                stmtInsert.setInt(2, idOrdine);
                
                if (idPersonale != null) {
                    stmtInsert.setInt(3, idPersonale);
                } else {
                    stmtInsert.setNull(3, java.sql.Types.INTEGER);
                }

                stmtInsert.executeUpdate();
                conn.commit(); 
                return true;
                
            } catch (SQLException e) {
                conn.rollback(); 
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Trova l'idPersonale a partire dall'idUtente memorizzato nel Token
     */
    private static Integer getIdPersonaleDaUtente(Connection conn, int idUtente) throws SQLException {
        String query = "SELECT idPersonale FROM Personale WHERE idUtente = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUtente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idPersonale");
                }
            }
        }
        return null;
    }
    
    /**
     *  Legge lo stato attuale di un ordine prima di modificarlo
     */
    public static String getStatoCorrente(int idOrdine) {
        String query = "SELECT statoCorrente FROM Ordine WHERE idOrdine = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("statoCorrente");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * permette di filtrare gli ordini per data e stato
     * @param dataInserimento
     * @param stato
     */
    public static List<Ordine> getOrdiniFiltrati(String dataInserimento, String stato) {
        List<Ordine> ordini = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Ordine WHERE 1=1");

        if (dataInserimento != null && !dataInserimento.isEmpty()) {
            queryBuilder.append(" AND DATE(timeInserimento) = ?"); 
        }
        if (stato != null && !stato.isEmpty()) {
            queryBuilder.append(" AND statoCorrente = ?");
        }
        queryBuilder.append(" ORDER BY orarioConsegnaRichiesto ASC");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            int paramIndex = 1;
            if (dataInserimento != null && !dataInserimento.isEmpty()) {
                stmt.setString(paramIndex++, dataInserimento);
            }
            if (stato != null && !stato.isEmpty()) {
                stmt.setString(paramIndex++, stato);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ordine o = new Ordine();
                    o.setIdOrdine(rs.getInt("idOrdine"));
                    o.setTimeInserimento(rs.getString("timeInserimento"));
                    o.setOrarioConsegnaRichiesto(rs.getString("orarioConsegnaRichiesto"));
                    o.setStatoCorrente(rs.getString("statoCorrente"));
                    o.setPrezzoTotale(rs.getDouble("prezzoTot"));
                    ordini.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }

    /**
     * Permette di ottenere tutti gli ordini di un cliente specifico, dato l'idUtente del token.
     * @param idUtente
     * @return List<Ordine>
     */
    public static List<Ordine> getOrdiniByCliente(int idUtente) {
        List<Ordine> ordini = new ArrayList<>();
        
        //devo mappare l'id utente con l'id cliente
        String query = "SELECT o.* FROM Ordine o " +
                       "JOIN Cliente c ON o.idCliente = c.idCliente " +
                       "WHERE c.idUtente = ? " +
                       "ORDER BY o.timeInserimento DESC";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idUtente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ordine o = new Ordine();
                    o.setIdOrdine(rs.getInt("idOrdine"));
                    o.setTimeInserimento(rs.getString("timeInserimento"));
                    o.setOrarioConsegnaRichiesto(rs.getString("orarioConsegnaRichiesto"));
                    o.setStatoCorrente(rs.getString("statoCorrente"));
                    o.setPrezzoTotale(rs.getDouble("prezzoTot")); 
                    ordini.add(o);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordini;
    }
    
    /**
     * Recupera l'idUtente proprietario di un ordine
     */
    public static Integer getIdUtenteProprietarioOrdine(int idOrdine) {
        String query = "SELECT c.idUtente FROM Ordine o " +
                       "JOIN Cliente c ON o.idCliente = c.idCliente " +
                       "WHERE o.idOrdine = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("idUtente");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Estrazione dettagli ordine
     */
    public static List<DettaglioProdotto> getDettagliOrdine(int idOrdine) {
        List<DettaglioProdotto> dettagli = new ArrayList<>();
        
        String query = "SELECT dp.idDettaglio, p.nome AS nomeProdotto, dp.quantita, p.tempoPreparazione, p.procedura " +
                       "FROM DettaglioProdotto dp " +
                       "JOIN Prodotto p ON dp.idProdotto = p.idProdotto " +
                       "WHERE dp.idOrdine = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DettaglioProdotto dp = new DettaglioProdotto();
                    dp.setIdDettaglio(rs.getInt("idDettaglio"));
                    dp.setNomeProdotto(rs.getString("nomeProdotto"));
                    dp.setQuantita(rs.getInt("quantita"));
                    dp.setTempoPreparazione(rs.getInt("tempoPreparazione"));
                    dp.setProcedura(rs.getString("procedura"));

                    dp.setCaratteristiche(getCaratteristicheTestualiPerDettaglio(conn, dp.getIdDettaglio()));

                    dettagli.add(dp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dettagli;
    }

    /**
     * Recupera solo i nomi delle caratteristiche per il JSON
     */
    private static List<String> getCaratteristicheTestualiPerDettaglio(Connection conn, int idDettaglio) {
        List<String> caratt = new ArrayList<>();
        String query = "SELECT c.nome FROM AssociazioneCaratteristica ac " +
                       "JOIN Caratteristica c ON ac.idCaratteristica = c.idCaratteristica " +
                       "WHERE ac.idDettaglio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idDettaglio);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caratt.add(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return caratt;
    }
    
    /**
     * Estrazione degli operatori che hanno gestito un ordine, per lo storico
     * @param idOrdine
     */
    public static List<OperatoreResponse> getOperatoriOrdine(int idOrdine) {
        List<OperatoreResponse> operatori = new ArrayList<>();
        
        String query = "SELECT DISTINCT u.nome, u.cognome FROM CambioStato cs " +
                       "JOIN Personale p ON cs.idPersonale = p.idPersonale " +
                       "JOIN Utente u ON p.idUtente = u.idUtente " +
                       "WHERE cs.idOrdine = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idOrdine);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OperatoreResponse op = new OperatoreResponse();
                    op.setNome(rs.getString("nome"));
                    op.setCognome(rs.getString("cognome"));
                    operatori.add(op);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operatori;
    }

}

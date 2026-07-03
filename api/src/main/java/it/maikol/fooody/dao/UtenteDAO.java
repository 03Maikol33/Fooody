/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.dao;

import it.maikol.fooody.models.Utente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author Maikol
 */
public class UtenteDAO {

    /**
     * Cerca un utente nel database usando email e password.
     * Se lo trova, restituisce l'oggetto Utente. Altrimenti restituisce null.
     */
    public static Utente login(String email, String password) {
        Utente utenteTrovato = null;
        
        String query = "SELECT idUtente, email, nome, cognome, ruolo " +
                       "FROM Utente " +
                       "WHERE email = ? AND password = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    utenteTrovato = new Utente();
                    utenteTrovato.setIdUtente(rs.getInt("idUtente"));
                    utenteTrovato.setEmail(rs.getString("email"));
                    utenteTrovato.setNome(rs.getString("nome"));
                    utenteTrovato.setCognome(rs.getString("cognome"));
                    utenteTrovato.setRuolo(rs.getString("ruolo"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il login nel database.");
            e.printStackTrace();
        }
        
        return utenteTrovato;
    }
    
    /**
     * Inserisce un nuovo utente nel database.
     * Ritorna true se l'inserimento va a buon fine, false altrimenti.
     */
    public static boolean registraUtente(Utente nuovoUtente) {
        String queryUtente = "INSERT INTO Utente (nome, cognome, email, password, ruolo) VALUES (?, ?, ?, ?, ?)";
        String queryCliente = "INSERT INTO Cliente (idUtente, telefono, via, civico, citta) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            //inizio transazione
            conn.setAutoCommit(false); 
            int idUtenteGenerato = -1;

            try (PreparedStatement stmtUtente = conn.prepareStatement(queryUtente, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmtUtente.setString(1, nuovoUtente.getNome());
                stmtUtente.setString(2, nuovoUtente.getCognome());
                stmtUtente.setString(3, nuovoUtente.getEmail());
                stmtUtente.setString(4, nuovoUtente.getPassword());
                stmtUtente.setString(5, "cliente");
                
                int affectedRows = stmtUtente.executeUpdate();
                if (affectedRows == 0) {
                    conn.rollback();
                    return false;
                }
                
                try (ResultSet generatedKeys = stmtUtente.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idUtenteGenerato = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            //inserimento cliente e associazione id utente
            try (PreparedStatement stmtCliente = conn.prepareStatement(queryCliente)) {
                stmtCliente.setInt(1, idUtenteGenerato);
                stmtCliente.setString(2, nuovoUtente.getTelefono() != null ? nuovoUtente.getTelefono() : "");
                stmtCliente.setString(3, nuovoUtente.getVia() != null ? nuovoUtente.getVia() : "Indirizzo generico");
                stmtCliente.setString(4, nuovoUtente.getCivico() != null ? nuovoUtente.getCivico() : "SN");
                stmtCliente.setString(5, nuovoUtente.getCitta() != null ? nuovoUtente.getCitta() : "Alba Adriatica");
                
                stmtCliente.executeUpdate();
            }
            
            conn.commit(); 
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Errore durante la registrazione: transazione annullata.");
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Aggiornamento dei dati personali
     */
    public static boolean aggiornaProfilo(Utente u) {
        String updateUtente = "UPDATE Utente SET nome = ?, cognome = ? WHERE idUtente = ?";
        String updateCliente = "UPDATE Cliente SET telefono = ?, via = ?, civico = ?, citta = ? WHERE idUtente = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            //parte utente
            try (PreparedStatement stmtU = conn.prepareStatement(updateUtente)) {
                stmtU.setString(1, u.getNome());
                stmtU.setString(2, u.getCognome());
                stmtU.setInt(3, u.getIdUtente());
                stmtU.executeUpdate();
            }

            //parte cliente
            try (PreparedStatement stmtC = conn.prepareStatement(updateCliente)) {
                stmtC.setString(1, u.getTelefono());
                stmtC.setString(2, u.getVia());
                stmtC.setString(3, u.getCivico());
                stmtC.setString(4, u.getCitta());
                stmtC.setInt(5, u.getIdUtente());
                stmtC.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
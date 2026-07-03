/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.dao;
import it.maikol.fooody.models.Personale;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Maikol
 */
public class PersonaleDAO {
    
    /**
     * Restituisce tutto il personale
     * @return 
     */
    public static List<Personale> getTuttoIlPersonale() {
        List<Personale> staff = new ArrayList<>();
        String query = "SELECT p.idPersonale, p.idUtente, u.nome, u.cognome FROM Personale p JOIN Utente u ON p.idUtente = u.idUtente";
        
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Personale p = new Personale();
                p.setIdPersonale(rs.getInt("idPersonale"));
                p.setIdUtente(rs.getInt("idUtente"));
                p.setNome(rs.getString("nome"));
                p.setCognome(rs.getString("cognome"));
                staff.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return staff;
    }

    /**
     * Rimuove un membro del personale
     * @param idPersonale
     * @return 
     */
    public static boolean rimuoviPersonale(int idPersonale) {
        String query = "DELETE FROM Personale WHERE idPersonale = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idPersonale);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    /**
     * Aggiunge un nuovo membro del personale promuovendo un utente già esistente
     * @param idUtente
     * @return 
     */
    public static boolean aggiungiPersonale(int idUtente) {
        String query = "INSERT INTO Personale (idUtente) VALUES (?)";
        String updateRuolo = "UPDATE Utente SET ruolo = 'personale' WHERE idUtente = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmtU = conn.prepareStatement(updateRuolo)) {
                stmtU.setInt(1, idUtente);
                stmtU.executeUpdate();
            }

            try (PreparedStatement stmtP = conn.prepareStatement(query)) {
                stmtP.setInt(1, idUtente);
                stmtP.executeUpdate();
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

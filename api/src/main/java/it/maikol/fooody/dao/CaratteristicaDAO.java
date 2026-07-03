/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.dao;
import it.maikol.fooody.models.Caratteristica;
import it.maikol.fooody.models.GruppoMutuaEsclusione;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Maikol
 */
public class CaratteristicaDAO {
    
    /**
     * Inserimento gruppo
     * @param gruppo
     * @return 
     */
    public static boolean inserisciGruppo(GruppoMutuaEsclusione gruppo) {
        String query = "INSERT INTO GruppoMutuaEsclusione (nome, descrizione) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, gruppo.getNome());
            stmt.setString(2, gruppo.getDescrizione());
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserimento caratteristica
     * @param c
     * @return 
     */
    public static boolean inserisciCaratteristica(Caratteristica c) {
        String query = "INSERT INTO Caratteristica (nome, descrizione, differenzaPrezzo, isDefault, idGmc) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getDescrizione()); 
            stmt.setDouble(3, c.getDifferenzaPrezzo());
            stmt.setBoolean(4, c.isDefault());
            
            //se una caratteristica non appartiene a nessun gruppo
            if (c.getidGmc() != null && c.getidGmc() > 0) {
                stmt.setInt(5, c.getidGmc());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore: Possibile nome caratteristica duplicato.");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Ottieni tutti i gruppi di mutua esclusione creati
     * @return 
     */
    public static List<GruppoMutuaEsclusione> getTuttiIGruppi() {
        List<GruppoMutuaEsclusione> gruppi = new ArrayList<>();
        String query = "SELECT * FROM GruppoMutuaEsclusione";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                GruppoMutuaEsclusione g = new GruppoMutuaEsclusione();
                g.setIdGmc(rs.getInt("idGmc"));
                g.setNome(rs.getString("nome"));
                g.setDescrizione(rs.getString("descrizione"));
                gruppi.add(g);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return gruppi;
    }

    /**
     * Aggiorna un gruppo di mutua esclusione
     * @param gruppo
     * @return 
     */
    public static boolean aggiornaGruppo(GruppoMutuaEsclusione gruppo) {
        String query = "UPDATE GruppoMutuaEsclusione SET nome=?, descrizione=? WHERE idGmc=?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, gruppo.getNome());
            stmt.setString(2, gruppo.getDescrizione());
            stmt.setInt(3, gruppo.getIdGmc());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * elimina un gruppo di mutua esclusione
     * @param idGmc
     * @return 
     */
    public static boolean eliminaGruppo(int idGmc) {
        String query = "DELETE FROM GruppoMutuaEsclusione WHERE idGmc=?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idGmc);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { 
            System.err.println("Impossibile eliminare: potrebbero esserci caratteristiche collegate.");
            e.printStackTrace(); 
            return false; 
        }
    }

    /**
     * ottieni tutte le caratteristiche presenti
     * @return 
     */
    public static List<Caratteristica> getTutteLeCaratteristiche() {
        List<Caratteristica> caratteristiche = new ArrayList<>();
        String query = "SELECT c.*, g.nome AS nomeGruppo FROM Caratteristica c LEFT JOIN GruppoMutuaEsclusione g ON c.idGmc = g.idGmc";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Caratteristica c = new Caratteristica();
                c.setIdCaratteristica(rs.getInt("idCaratteristica"));
                c.setNome(rs.getString("nome"));
                c.setDifferenzaPrezzo(rs.getDouble("differenzaPrezzo"));
                c.setIsDefault(rs.getBoolean("isDefault"));
                int idGmc = rs.getInt("idGmc");
                if (!rs.wasNull()) {
                    c.setidGmc(idGmc);
                }
                caratteristiche.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return caratteristiche;
    }
    
    /**
     * aggiorna una caratteristica
     * @param c
     * @return 
     */
    public static boolean aggiornaCaratteristica(Caratteristica c) {
        String query = "UPDATE Caratteristica SET nome=?, differenzaPrezzo=?, isDefault=?, idGmc=? WHERE idCaratteristica=?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, c.getNome());
            stmt.setDouble(2, c.getDifferenzaPrezzo());
            stmt.setBoolean(3, c.isDefault());
            if (c.getidGmc() != null && c.getidGmc() > 0) {
                stmt.setInt(4, c.getidGmc());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setInt(5, c.getIdCaratteristica());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    /**
     * elimina una caratteristica
     * @param idCaratteristica
     * @return 
     */
    public static boolean eliminaCaratteristica(int idCaratteristica) {
        String query = "DELETE FROM Caratteristica WHERE idCaratteristica=?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCaratteristica);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}

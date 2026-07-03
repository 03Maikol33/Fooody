/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.dao;
import it.maikol.fooody.models.Ingrediente;
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
public class IngredienteDAO {
    
    /**
     * Ottieni tutti gli ingredienti presenti nel db
     * @return 
     */
    public static List<Ingrediente> getTuttiIngredienti() {
        List<Ingrediente> lista = new ArrayList<>();
        String query = "SELECT idIngrediente, nome FROM Ingrediente ORDER BY nome ASC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Ingrediente i = new Ingrediente();
                i.setIdIngrediente(rs.getInt("idIngrediente"));
                i.setNome(rs.getString("nome"));
                lista.add(i);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public static boolean inserisciIngrediente(Ingrediente i) {
        String query = "INSERT INTO Ingrediente (nome) VALUES (?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, i.getNome());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Elimina un ingrediente esistente
     * @param idIngrediente
     * @return 
     */
    public static boolean eliminaIngrediente(int idIngrediente) {
        String query = "DELETE FROM Ingrediente WHERE idIngrediente = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idIngrediente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}

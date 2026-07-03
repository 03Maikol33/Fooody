/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.dao;
import it.maikol.fooody.models.Caratteristica;
import it.maikol.fooody.models.Ingrediente;
import it.maikol.fooody.models.Prodotto;
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
public class ProdottoDAO {
    /**
     * Estrazione del menu con filtri opzionali: categoria, nome, prezzo minimo e massimo.
     * @param categoria
     * @param nome
     * @param prezzoMin
     * @param prezzoMax
     * @return List<Prodotto>
     */
    public static List<Prodotto> getMenu(String categoria, String nome, Double prezzoMin, Double prezzoMax) {
        List<Prodotto> prodotti = new ArrayList<>();
        
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM Prodotto WHERE 1=1");
        
        if (categoria != null && !categoria.isEmpty()) queryBuilder.append(" AND categoria = ?");
        if (nome != null && !nome.isEmpty()) queryBuilder.append(" AND nome LIKE ?");
        if (prezzoMin != null) queryBuilder.append(" AND prezzoBase >= ?");
        if (prezzoMax != null) queryBuilder.append(" AND prezzoBase <= ?");

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            if (categoria != null && !categoria.isEmpty()) stmt.setString(paramIndex++, categoria);
            if (nome != null && !nome.isEmpty()) stmt.setString(paramIndex++, "%" + nome + "%");
            if (prezzoMin != null) stmt.setDouble(paramIndex++, prezzoMin);
            if (prezzoMax != null) stmt.setDouble(paramIndex++, prezzoMax);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prodotto p = new Prodotto();
                    p.setIdProdotto(rs.getInt("idProdotto"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setNome(rs.getString("nome"));
                    p.setDescrizione(rs.getString("descrizione"));
                    p.setPrezzoBase(rs.getDouble("prezzoBase"));
                    p.setImmagine(rs.getString("immagine"));
                    p.setTempoPreparazione(rs.getInt("tempoPreparazione"));
                    p.setProcedura(rs.getString("procedura"));
                    
                    p.setCaratteristiche(getCaratteristicheProdotto(conn, p.getIdProdotto()));
                    
                    prodotti.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prodotti;
    }

    /**
     * Metodo di supporto: Estrae le caratteristiche associate a un prodotto specifico.
     * @param conn
     * @param idProdotto
     * @return List<Caratteristica>
     */
    private static List<Caratteristica> getCaratteristicheProdotto(Connection conn, int idProdotto) {
        List<Caratteristica> caratteristiche = new ArrayList<>();
        String query = "SELECT c.*, g.nome AS nomeGruppo FROM Caratteristica c " +
                       "JOIN PossessoCaratteristica pc ON c.idCaratteristica = pc.idCaratteristica " +
                       "LEFT JOIN GruppoMutuaEsclusione g ON c.idGmc = g.idGmc " +
                       "WHERE pc.idProdotto = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idProdotto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Caratteristica c = new Caratteristica();
                    c.setIdCaratteristica(rs.getInt("idCaratteristica"));
                    c.setNome(rs.getString("nome"));
                    c.setDifferenzaPrezzo(rs.getDouble("differenzaPrezzo"));
                    c.setIsDefault(rs.getBoolean("isDefault"));

                    int idGmc = rs.getInt("idGmc");
                    if (!rs.wasNull()) {
                        c.setidGmc(idGmc);
                    } else {
                        c.setidGmc(null);
                    }
                    caratteristiche.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return caratteristiche;
    }

    /**
     * Rimuove una caratteristica da un prodotto specifico.
     */
    public static boolean eliminaCaratteristicaDaProdotto(int idProdotto, int idCaratteristica) {
        String query = "DELETE FROM PossessoCaratteristica WHERE idProdotto = ? AND idCaratteristica = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idProdotto);
            stmt.setInt(2, idCaratteristica);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera gli ingredienti associati a un prodotto specifico.
     * @param idProdotto l'ID del prodotto
     * @return la lista degli ingredienti associati
     */
    public static List<Ingrediente> getIngredientiProdotto(int idProdotto) {
        List<Ingrediente> ingredienti = new ArrayList<>();
        String query = "SELECT i.idIngrediente, i.nome, cp.quantita FROM Ingrediente i " +
                       "JOIN ComposizioneProdotto cp ON i.idIngrediente = cp.idIngrediente " +
                       "WHERE cp.idProdotto = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idProdotto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ingrediente ing = new Ingrediente();
                    ing.setIdIngrediente(rs.getInt("idIngrediente"));
                    ing.setNome(rs.getString("nome"));
                    ing.setQuantita(rs.getString("quantita"));
                    ingredienti.add(ing);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredienti;
    }
    
    /**
     * Inserimento prodotto
     * @param p il prodotto da inserire
     * @return true se l'inserimento è avvenuto con successo, false altrimenti
     */
    public static boolean inserisciProdotto(Prodotto p) {
        String query = "INSERT INTO Prodotto (nome, categoria, descrizione, prezzoBase, tempoPreparazione, procedura, immagine) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setString(3, p.getDescrizione());
            stmt.setDouble(4, p.getPrezzoBase());
            stmt.setInt(5, p.getTempoPreparazione());
            stmt.setString(6, p.getProcedura());
            stmt.setString(7, p.getImmagine());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Aggiornamento prodotto esistente
     * @param p prodotto da aggiornare
     * @return 
     */
    public static boolean aggiornaProdotto(Prodotto p) {
        String query = "UPDATE Prodotto SET nome=?, categoria=?, descrizione=?, prezzoBase=?, tempoPreparazione=?, procedura=?, immagine=? WHERE idProdotto=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCategoria());
            stmt.setString(3, p.getDescrizione());
            stmt.setDouble(4, p.getPrezzoBase());
            stmt.setInt(5, p.getTempoPreparazione());
            stmt.setString(6, p.getProcedura());
            stmt.setString(7, p.getImmagine());
            stmt.setInt(8, p.getIdProdotto());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Assegna una caratteristica già esistente a un prodotto.
     * Scrive nella tabella PossessoCaratteristica.
     */
    public static boolean assegnaCaratteristicaAProdotto(int idProdotto, int idCaratteristica) {
        String query = "INSERT INTO PossessoCaratteristica (idProdotto, idCaratteristica) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idProdotto);
            stmt.setInt(2, idCaratteristica);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore: Impossibile associare la caratteristica. Potrebbe essere già associata o non esistere.");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Ottieni tutte le categorie di prodotti presenti nel db
     * @return 
     */
    public static List<String> getTutteLeCategorie() {
        List<String> categorie = new ArrayList<>();
        String query = "SELECT DISTINCT categoria FROM Prodotto WHERE categoria IS NOT NULL AND categoria != ''";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categorie.add(rs.getString("categoria"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorie;
    }
    
    /**
     * Associazione di un ingrediente esistente ad un prodotto
     * @param idProdotto
     * @param idIngrediente
     * @param quantita
     * @return 
     */
    public static boolean associaIngredienteAProdotto(int idProdotto, int idIngrediente, String quantita) {
        String query = "INSERT INTO ComposizioneProdotto (idProdotto, idIngrediente, quantita) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idProdotto);
            stmt.setInt(2, idIngrediente);
            stmt.setString(3, quantita);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /**
     * Dissociazione di un ingrediente esistente da un prodotto
     * @param idProdotto
     * @param idIngrediente
     * @return 
     */
    public static boolean rimuoviIngredienteDaProdotto(int idProdotto, int idIngrediente) {
        String query = "DELETE FROM ComposizioneProdotto WHERE idProdotto = ? AND idIngrediente = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idProdotto);
            stmt.setInt(2, idIngrediente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}

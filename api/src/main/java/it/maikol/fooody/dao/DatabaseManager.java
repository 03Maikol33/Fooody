/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.maikol.fooody.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/WebDelivery?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    
    //singleton
    private static Connection connection = null;

    /**
     * Per ottenere la connessione al database.
     * Se è chiusa o non esiste, la crea. Altrimenti restituisce quella attiva.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Connessione a MySQL stabilita con successo");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Errore: Driver MySQL non trovato");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Impossibile connettersi al database.");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Per chiudere la connessione al database quando il server si spegne.
     */
    public static void chiudiConnessione() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connessione a MySQL chiusa.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

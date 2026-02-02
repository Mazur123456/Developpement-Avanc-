package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pour la gestion de la connexion à la base de données PostgreSQL
 */
public class ConnectionDB {
    private static final String URL = "jdbc:postgresql://localhost:5432/MasterAnnonce";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password13";

    private static Connection connection = null;

    /**
     * Constructeur privé pour empêcher l'instanciation directe
     */
    private ConnectionDB() throws ClassNotFoundException, SQLException {
        // Chargement du driver PostgreSQL
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Retourne l'instance unique de la connexion
     * 
     * @return Connection - instance de connexion à la base de données
     * @throws ClassNotFoundException si le driver PostgreSQL n'est pas trouvé
     */
    public static Connection getInstance() throws ClassNotFoundException {
        if (connection == null) {
            try {
                new ConnectionDB();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
            }
        }
        return connection;
    }
}

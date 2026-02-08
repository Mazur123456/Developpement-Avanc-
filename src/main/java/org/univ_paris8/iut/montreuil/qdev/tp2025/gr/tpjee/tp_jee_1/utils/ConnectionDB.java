package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pour la gestion de la connexion à la base de données PostgreSQL
 * Supporte Docker (variables d'environnement) et développement local (fallback)
 */
public class ConnectionDB {
    // Configuration avec support Docker et fallback local
    private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "5432";
    private static final String DB_NAME = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME")
            : "master_annonce";
    private static final String URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    private static final String USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD")
            : "postgres";

    private static Connection connection = null;

    /**
     * Constructeur privé pour empêcher l'instanciation directe
     */
    /**
     * Constructeur privé pour empêcher l'instanciation directe
     */
    private ConnectionDB() {
    }

    /**
     * Retourne l'instance unique de la connexion
     * 
     * @return Connection - instance de connexion à la base de données
     * @throws ClassNotFoundException si le driver PostgreSQL n'est pas trouvé
     */
    public static Connection getInstance() throws ClassNotFoundException {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("[DEBUG] Création nouvelle connexion BDD...");
                System.out.println("[DEBUG] URL: " + URL);
                System.out.println("[DEBUG] USER: " + USER);

                // Chargement explicite du driver (optionnel mais sûr)
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DEBUG] Connexion réussie !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la connexion à la base de données: " + e.getMessage(), e);
        }
        return connection;
    }
}

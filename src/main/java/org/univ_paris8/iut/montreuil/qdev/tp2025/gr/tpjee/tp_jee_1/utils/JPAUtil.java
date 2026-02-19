package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitaire pour la gestion des EntityManager JPA.
 * Fournit un accès centralisé à l'EntityManagerFactory et aux EntityManager.
 */
public class JPAUtil {

    private static final Logger logger = LoggerFactory.getLogger(JPAUtil.class);
    private static final String PERSISTENCE_UNIT_NAME = "MasterAnnoncePU";
    private static EntityManagerFactory entityManagerFactory;

    private JPAUtil() {
        // Classe utilitaire
    }

    // Initialisation statique de l'EntityManagerFactory
    static {
        try {
            Map<String, String> properties = new HashMap<>();

            // Récupération des variables d'environnement (pour Docker)
            String dbHost = System.getenv("DB_HOST");
            if (dbHost != null && !dbHost.isEmpty()) {
                String dbPort = System.getenv("DB_PORT");
                String dbName = System.getenv("DB_NAME");
                String dbUser = System.getenv("DB_USER");
                String dbPassword = System.getenv("DB_PASSWORD");

                if (dbPort == null)
                    dbPort = "5432";

                String url = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;

                properties.put("javax.persistence.jdbc.url", url);
                properties.put("javax.persistence.jdbc.user", dbUser);
                properties.put("javax.persistence.jdbc.password", dbPassword);

                logger.info("Configuration Docker détectée : {}", url);
            }

            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME, properties);
            logger.info("EntityManagerFactory créé avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'EntityManagerFactory", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Retourne l'EntityManagerFactory singleton
     *
     * @return EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    /**
     * Crée et retourne un nouvel EntityManager.
     * L'appelant est responsable de fermer l'EntityManager après utilisation.
     *
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Ferme l'EntityManagerFactory.
     * À appeler lors de l'arrêt de l'application.
     */
    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            logger.info("EntityManagerFactory fermé");
        }
    }
}

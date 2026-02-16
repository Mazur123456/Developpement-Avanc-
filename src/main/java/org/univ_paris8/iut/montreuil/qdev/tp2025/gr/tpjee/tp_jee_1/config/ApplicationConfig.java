package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Point d'entrée JAX-RS.
 * Toutes les routes REST commenceront par /api.
 *
 * Justification du choix Jersey :
 * Jersey est l'implémentation de référence de la spécification JAX-RS.
 * Il est parfaitement compatible avec Tomcat (servlet container) et
 * ne nécessite pas de serveur d'application complet comme WildFly.
 * Il offre un support natif pour Jackson (JSON), les filtres, et la validation.
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {
    // Jersey scanne automatiquement les classes annotées @Path et @Provider
}

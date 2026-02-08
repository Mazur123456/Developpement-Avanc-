package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.listener;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.JPAUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Listener pour gérer le cycle de vie de l'EntityManagerFactory.
 * Ferme proprement les connexions JPA à l'arrêt de l'application.
 */
@WebListener
public class JPAContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // L'EntityManagerFactory est initialisé via le bloc static de JPAUtil
        System.out.println("[JPA] Application démarrée - JPAUtil initialisé");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Ferme l'EntityManagerFactory proprement
        JPAUtil.shutdown();
        System.out.println("[JPA] Application arrêtée - Connexions JPA fermées");
    }
}

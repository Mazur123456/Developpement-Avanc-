package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    // MODIFIE CECI : Mets le chemin absolu vers ton fichier .db
    private static String url = "jdbc:sqlite:/home/etudiants/info/jmazur/MasterAnnonce.db";
    private static Connection connect;

    private ConnectionDB() throws ClassNotFoundException {
        try {
            Class.forName("org.sqlite.JDBC");
            connect = DriverManager.getConnection(url);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static Connection getInstance() throws ClassNotFoundException {
        if (connect == null) { new ConnectionDB(); }
        return connect;
    }
}
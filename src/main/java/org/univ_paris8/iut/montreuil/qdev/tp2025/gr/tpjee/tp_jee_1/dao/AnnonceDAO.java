package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dao;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.ConnectionDB;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.bean.Annonce;

import java.sql.*;
import java.util.ArrayList;


public class AnnonceDAO {

    public void create(Annonce obj) {
        try {
            Connection conn = ConnectionDB.getInstance();
            String sql = "INSERT INTO annonce (title, description, adress, mail) VALUES (?, ?, ?, ?)";
            PreparedStatement prepare = conn.prepareStatement(sql);
            prepare.setString(1, obj.getTitle());
            prepare.setString(2, obj.getDescription());
            prepare.setString(3, obj.getAdress());
            prepare.setString(4, obj.getMail());
            prepare.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Annonce> list() {
        ArrayList<Annonce> liste = new ArrayList<>();
        try {
            Connection conn = ConnectionDB.getInstance();
            ResultSet result = conn.createStatement().executeQuery("SELECT * FROM annonce ORDER BY date DESC");
            while (result.next()) {
                Annonce a = new Annonce();
                a.setId(result.getInt("id"));
                a.setTitle(result.getString("title"));
                a.setDescription(result.getString("description"));
                a.setAdress(result.getString("adress"));
                a.setMail(result.getString("mail"));
                liste.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return liste;
    }
}
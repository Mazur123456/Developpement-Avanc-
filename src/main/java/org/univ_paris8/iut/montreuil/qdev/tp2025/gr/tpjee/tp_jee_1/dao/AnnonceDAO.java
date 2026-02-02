package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dao;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.bean.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des annonces
 */
public class AnnonceDAO extends DAO<Annonce> {

    @Override
    public Annonce find(int id) {
        Annonce annonce = null;
        try {
            Connection conn = ConnectionDB.getInstance();
            String sql = "SELECT * FROM annonce WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                annonce = new Annonce();
                annonce.setId(rs.getInt("id"));
                annonce.setTitle(rs.getString("title"));
                annonce.setDescription(rs.getString("description"));
                annonce.setAdress(rs.getString("adress"));
                annonce.setMail(rs.getString("mail"));
                annonce.setDate(rs.getTimestamp("date"));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return annonce;
    }

    @Override
    public boolean create(Annonce obj) {
        try {
            Connection conn = ConnectionDB.getInstance();
            String sql = "INSERT INTO annonce (title, description, adress, mail) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, obj.getTitle());
            stmt.setString(2, obj.getDescription());
            stmt.setString(3, obj.getAdress());
            stmt.setString(4, obj.getMail());

            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Annonce obj) {
        try {
            Connection conn = ConnectionDB.getInstance();
            String sql = "UPDATE annonce SET title = ?, description = ?, adress = ?, mail = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, obj.getTitle());
            stmt.setString(2, obj.getDescription());
            stmt.setString(3, obj.getAdress());
            stmt.setString(4, obj.getMail());
            stmt.setInt(5, obj.getId());

            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Annonce obj) {
        try {
            Connection conn = ConnectionDB.getInstance();
            String sql = "DELETE FROM annonce WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, obj.getId());

            int result = stmt.executeUpdate();
            stmt.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Annonce> findAll() {
        List<Annonce> annonces = new ArrayList<>();
        try {
            Connection conn = ConnectionDB.getInstance();
            String sql = "SELECT * FROM annonce ORDER BY date DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Annonce annonce = new Annonce();
                annonce.setId(rs.getInt("id"));
                annonce.setTitle(rs.getString("title"));
                annonce.setDescription(rs.getString("description"));
                annonce.setAdress(rs.getString("adress"));
                annonce.setMail(rs.getString("mail"));
                annonce.setDate(rs.getTimestamp("date"));
                annonces.add(annonce);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return annonces;
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.bean;

import java.sql.Timestamp;

/**
 * Bean représentant une annonce
 */
public class Annonce {
    private int id;
    private String title;
    private String description;
    private String adress;
    private String mail;
    private Timestamp date;

    /**
     * Constructeur vide
     */
    public Annonce() {
    }

    /**
     * Constructeur complet
     */
    public Annonce(int id, String title, String description, String adress, String mail, Timestamp date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.adress = adress;
        this.mail = mail;
        this.date = date;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.bean;

import java.util.Date;

public class Annonce {
    private int id;
    private String title;
    private String description;
    private String adress;
    private String mail;
    private Date date;

    // Constructeur vide
    public Annonce() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAdress() { return adress; }
    public void setAdress(String adress) { this.adress = adress; }
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}
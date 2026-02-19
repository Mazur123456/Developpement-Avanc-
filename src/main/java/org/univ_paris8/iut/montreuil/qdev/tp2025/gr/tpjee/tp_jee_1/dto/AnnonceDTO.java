package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceConstants;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

/**
 * DTO (Data Transfer Object) pour l'entité Annonce.
 * Sépare la couche REST de la couche JPA :
 * - On n'expose pas les entités JPA directement au client
 * - On contrôle exactement les champs envoyés/reçus en JSON
 *
 * Les annotations @NotBlank, @Size, @Email servent à la validation
 * automatique via Bean Validation (JSR-380).
 * Quand on met @Valid dans la Resource, Jersey valide avant d'exécuter la
 * méthode.
 */
public class AnnonceDTO {

    private Long id;

    @NotBlank(message = AnnonceConstants.TITLE_REQUIRED)
    @Size(max = AnnonceConstants.TITLE_MAX, message = AnnonceConstants.TITLE_TOO_LONG)
    private String title;

    @Size(max = AnnonceConstants.DESCRIPTION_MAX, message = AnnonceConstants.DESCRIPTION_TOO_LONG)
    private String description;

    @Size(max = AnnonceConstants.ADRESS_MAX, message = AnnonceConstants.ADRESS_TOO_LONG)
    private String adress;

    @Email(message = AnnonceConstants.MAIL_INVALID)
    @Size(max = AnnonceConstants.MAIL_MAX, message = AnnonceConstants.MAIL_TOO_LONG)
    private String mail;

    private String status;
    private Timestamp date;

    // Infos auteur (on expose le nom, pas le mot de passe !)
    private Long authorId;
    private String authorUsername;

    // Infos catégorie
    private Long categoryId;
    private String categoryLabel;

    // Version pour la gestion de concurrence optimiste (@Version)
    private Long version;

    // Constructeurs
    public AnnonceDTO() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

/**
 * Entité JPA représentant une annonce
 */
@Entity
@Table(name = "annonce")
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = AnnonceConstants.TITLE_REQUIRED)
    @Size(max = AnnonceConstants.TITLE_MAX, message = AnnonceConstants.TITLE_TOO_LONG)
    @Column(nullable = false, length = AnnonceConstants.TITLE_MAX)
    private String title;

    @Size(max = AnnonceConstants.DESCRIPTION_MAX, message = AnnonceConstants.DESCRIPTION_TOO_LONG)
    @Column(length = AnnonceConstants.DESCRIPTION_MAX)
    private String description;

    @Size(max = AnnonceConstants.ADRESS_MAX, message = AnnonceConstants.ADRESS_TOO_LONG)
    @Column(length = AnnonceConstants.ADRESS_MAX)
    private String adress;

    @Email(message = AnnonceConstants.MAIL_INVALID)
    @Size(max = AnnonceConstants.MAIL_MAX, message = AnnonceConstants.MAIL_TOO_LONG)
    @Column(length = AnnonceConstants.MAIL_MAX)
    private String mail;

    @Column(name = "date")
    private Timestamp date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnnonceStatus status = AnnonceStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Version
    private Long version;

    // Constructeurs
    public Annonce() {
        this.date = new Timestamp(System.currentTimeMillis());
        this.status = AnnonceStatus.DRAFT;
    }

    public Annonce(String title, String description, String adress, String mail) {
        this();
        this.title = title;
        this.description = description;
        this.adress = adress;
        this.mail = mail;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public AnnonceStatus getStatus() {
        return status;
    }

    public void setStatus(AnnonceStatus status) {
        this.status = status;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Méthodes utilitaires pour changer le statut
    public void publish() {
        this.status = AnnonceStatus.PUBLISHED;
    }

    public void archive() {
        this.status = AnnonceStatus.ARCHIVED;
    }

    public boolean isDraft() {
        return this.status == AnnonceStatus.DRAFT;
    }

    public boolean isPublished() {
        return this.status == AnnonceStatus.PUBLISHED;
    }

    public boolean isArchived() {
        return this.status == AnnonceStatus.ARCHIVED;
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.sql.Timestamp;

/**
 * Entité JPA représentant une annonce
 */
@Entity
@Table(name = "annonce")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "annonce_seq")
    @SequenceGenerator(name = "annonce_seq", sequenceName = "annonce_id_seq", allocationSize = 1)
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
    @Builder.Default
    private Timestamp date = new Timestamp(System.currentTimeMillis());

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AnnonceStatus status = AnnonceStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @Version
    private Long version;

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

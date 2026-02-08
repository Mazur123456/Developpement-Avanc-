package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant une catégorie d'annonces
 */
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le libellé de la catégorie est obligatoire")
    @Size(max = 50, message = "Le libellé ne doit pas dépasser 50 caractères")
    @Column(unique = true, nullable = false, length = 50)
    private String label;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Annonce> annonces = new ArrayList<>();

    // Constructeurs
    public Category() {
    }

    public Category(String label) {
        this.label = label;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Annonce> getAnnonces() {
        return annonces;
    }

    public void setAnnonces(List<Annonce> annonces) {
        this.annonces = annonces;
    }
}

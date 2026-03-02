package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité JPA représentant une catégorie d'annonces
 */
@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(name = "category_seq", sequenceName = "category_id_seq", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Le libellé de la catégorie est obligatoire")
    @Size(max = 50, message = "Le libellé ne doit pas dépasser 50 caractères")
    @Column(unique = true, nullable = false, length = 50)
    private String label;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Annonce> annonces = new ArrayList<>();
}

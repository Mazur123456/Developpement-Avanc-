package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour la gestion des catégories.
 * Les transactions sont gérées par la couche Service, pas ici.
 */
public class CategoryRepository {

    /**
     * Persiste une nouvelle catégorie
     */
    public Category create(EntityManager em, Category category) {
        em.persist(category);
        return category;
    }

    /**
     * Trouve une catégorie par son ID
     */
    public Optional<Category> findById(EntityManager em, Long id) {
        Category category = em.find(Category.class, id);
        return Optional.ofNullable(category);
    }

    /**
     * Trouve une catégorie par son label
     */
    public Optional<Category> findByLabel(EntityManager em, String label) {
        try {
            TypedQuery<Category> query = em.createQuery(
                    "SELECT c FROM Category c WHERE c.label = :label", Category.class);
            query.setParameter("label", label);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Vérifie si un label existe déjà
     */
    public boolean existsByLabel(EntityManager em, String label) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE c.label = :label", Long.class);
        query.setParameter("label", label);
        return query.getSingleResult() > 0;
    }

    /**
     * Récupère toutes les catégories
     */
    public List<Category> findAll(EntityManager em) {
        TypedQuery<Category> query = em.createQuery(
                "SELECT c FROM Category c ORDER BY c.label ASC", Category.class);
        return query.getResultList();
    }

    /**
     * Met à jour une catégorie (merge)
     */
    public Category update(EntityManager em, Category category) {
        return em.merge(category);
    }

    /**
     * Supprime une catégorie
     */
    public void delete(EntityManager em, Long id) {
        Category category = em.find(Category.class, id);
        if (category != null) {
            em.remove(category);
        }
    }
}

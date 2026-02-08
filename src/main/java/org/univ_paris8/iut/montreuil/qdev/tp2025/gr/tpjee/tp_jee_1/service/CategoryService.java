package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.JPAUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des catégories
 * Les transactions sont gérées ici, pas dans les Servlets
 */
public class CategoryService {

    /**
     * Crée une nouvelle catégorie
     */
    public Category create(String label) throws ValidationUtil.ValidationException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Vérifier si le label existe déjà
            if (existsByLabel(em, label)) {
                throw new IllegalArgumentException("Cette catégorie existe déjà");
            }

            Category category = new Category(label);
            ValidationUtil.validateAndThrow(category);
            em.persist(category);
            em.getTransaction().commit();
            return category;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Trouve une catégorie par son ID
     */
    public Optional<Category> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Category category = em.find(Category.class, id);
            return Optional.ofNullable(category);
        } finally {
            em.close();
        }
    }

    /**
     * Trouve une catégorie par son label
     */
    public Optional<Category> findByLabel(String label) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery(
                    "SELECT c FROM Category c WHERE c.label = :label", Category.class);
            query.setParameter("label", label);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Récupère toutes les catégories
     */
    public List<Category> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Category c ORDER BY c.label ASC", Category.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Met à jour une catégorie
     */
    public Category update(Category category) throws ValidationUtil.ValidationException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            ValidationUtil.validateAndThrow(category);
            Category updated = em.merge(category);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Supprime une catégorie
     */
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Category category = em.find(Category.class, id);
            if (category != null) {
                em.remove(category);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Méthode utilitaire privée
    private boolean existsByLabel(EntityManager em, String label) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE c.label = :label", Long.class);
        query.setParameter("label", label);
        return query.getSingleResult() > 0;
    }
}

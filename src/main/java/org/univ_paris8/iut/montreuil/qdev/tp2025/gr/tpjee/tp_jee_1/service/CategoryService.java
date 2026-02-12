package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.DuplicateEntityException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.JPAUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des catégories.
 * Les transactions sont gérées ici, pas dans les Repositories ni les Servlets.
 * Délègue les opérations d'accès aux données au CategoryRepository.
 */
public class CategoryService {

    private final CategoryRepository categoryRepository = new CategoryRepository();

    /**
     * Crée une nouvelle catégorie
     */
    public Category create(String label) throws ValidationUtil.ValidationException, DuplicateEntityException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Vérifier si le label existe déjà
            if (categoryRepository.existsByLabel(em, label)) {
                throw new DuplicateEntityException("label", label);
            }

            Category category = new Category(label);
            ValidationUtil.validateAndThrow(category);
            categoryRepository.create(em, category);
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
            return categoryRepository.findById(em, id);
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
            return categoryRepository.findByLabel(em, label);
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
            return categoryRepository.findAll(em);
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
            Category updated = categoryRepository.update(em, category);
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
            categoryRepository.delete(em, id);
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
}

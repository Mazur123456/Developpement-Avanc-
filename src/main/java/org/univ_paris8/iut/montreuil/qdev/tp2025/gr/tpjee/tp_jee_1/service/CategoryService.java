package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.DuplicateEntityException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.TransactionTemplate;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service métier pour la gestion des catégories.
 * Les transactions sont gérées via TransactionTemplate.
 */
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository = new CategoryRepository();

    /**
     * Crée une nouvelle catégorie
     */
    public Category create(String label) throws ValidationUtil.ValidationException, DuplicateEntityException {
        return TransactionTemplate.executeInTransaction(em -> {
            if (categoryRepository.existsByLabel(em, label)) {
                throw new DuplicateEntityException("label", label);
            }

            Category category = new Category(label);
            ValidationUtil.validateAndThrow(category);
            categoryRepository.create(em, category);
            logger.info("Catégorie créée [id={}, label={}]", category.getId(), label);
            return category;
        });
    }

    /**
     * Trouve une catégorie par son ID
     */
    public Optional<Category> findById(Long id) {
        return TransactionTemplate.executeReadOnly(em -> categoryRepository.findById(em, id));
    }

    /**
     * Trouve une catégorie par son label
     */
    public Optional<Category> findByLabel(String label) {
        return TransactionTemplate.executeReadOnly(em -> categoryRepository.findByLabel(em, label));
    }

    /**
     * Récupère toutes les catégories
     */
    public List<Category> findAll() {
        return TransactionTemplate.executeReadOnly(em -> categoryRepository.findAll(em));
    }

    /**
     * Met à jour une catégorie
     */
    public Category update(Category category) throws ValidationUtil.ValidationException {
        return TransactionTemplate.executeInTransaction(em -> {
            ValidationUtil.validateAndThrow(category);
            Category updated = categoryRepository.update(em, category);
            logger.info("Catégorie mise à jour [id={}]", category.getId());
            return updated;
        });
    }

    /**
     * Supprime une catégorie
     */
    public void delete(Long id) {
        TransactionTemplate.executeInTransactionVoid(em -> {
            categoryRepository.delete(em, id);
            logger.info("Catégorie supprimée [id={}]", id);
        });
    }
}

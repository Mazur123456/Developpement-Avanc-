package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.DuplicateEntityException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category create(String label) throws DuplicateEntityException {
        if (categoryRepository.existsByLabel(label)) {
            throw new DuplicateEntityException("label", label);
        }

        Category category = new Category(label);
        category = categoryRepository.save(category);
        log.info("Catégorie créée [id={}, label={}]", category.getId(), label);
        return category;
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> findByLabel(String label) {
        return categoryRepository.findByLabel(label);
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category update(Category category) {
        Category updated = categoryRepository.save(category);
        log.info("Catégorie mise à jour [id={}]", category.getId());
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
        log.info("Catégorie supprimée [id={}]", id);
    }
}

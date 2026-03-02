package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.InvalidStateException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceFilterDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.AnnonceSpecifications;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnonceService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Annonce create(String title, String description, String adress, String mail,
            Long authorId, Long categoryId) throws EntityNotFoundException {
        Annonce annonce = Annonce.builder()
                .title(title)
                .description(description)
                .adress(adress)
                .mail(mail)
                .build();

        if (authorId != null) {
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new EntityNotFoundException("User", authorId));
            annonce.setAuthor(author);
        }

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
            annonce.setCategory(category);
        }

        annonce = annonceRepository.save(annonce);
        log.info("Annonce créée avec succès [id={}, titre={}, auteur={}]",
                annonce.getId(), annonce.getTitle(), authorId);
        return annonce;
    }

    @Transactional
    public Annonce update(Long id, String title, String description, String adress,
            String mail, Long categoryId, Long userId) throws EntityNotFoundException {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce", id));

        checkOwnership(annonce, userId);

        if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
            throw new InvalidStateException("Une annonce publiée ne peut plus être modifiée");
        }

        annonce.setTitle(title);
        annonce.setDescription(description);
        annonce.setAdress(adress);
        annonce.setMail(mail);

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
            annonce.setCategory(category);
        }

        annonce = annonceRepository.save(annonce);
        log.info("Annonce mise à jour [id={}, par userId={}]", id, userId);
        return annonce;
    }

    @Transactional
    public Annonce publish(Long id, Long userId) throws EntityNotFoundException, InvalidStateException {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce", id));

        checkOwnership(annonce, userId);

        if (annonce.getStatus() != AnnonceStatus.DRAFT) {
            throw new InvalidStateException(annonce.getStatus().name(), AnnonceStatus.DRAFT.name());
        }

        annonce.publish();
        annonce = annonceRepository.save(annonce);
        log.info("Annonce publiée [id={}, par userId={}]", id, userId);
        return annonce;
    }

    @Transactional
    public Annonce archive(Long id, Long userId) throws EntityNotFoundException, InvalidStateException {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce", id));

        checkOwnership(annonce, userId);

        if (annonce.getStatus() != AnnonceStatus.PUBLISHED) {
            throw new InvalidStateException(annonce.getStatus().name(), AnnonceStatus.PUBLISHED.name());
        }

        annonce.archive();
        annonce = annonceRepository.save(annonce);
        log.info("Annonce archivée [id={}, par userId={}]", id, userId);
        return annonce;
    }

    @Transactional
    public void delete(Long id, Long userId) throws EntityNotFoundException {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce", id));

        checkOwnership(annonce, userId);

        if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
            throw new InvalidStateException(
                    "L'annonce doit être archivée avant suppression (statut actuel: "
                            + annonce.getStatus().name() + ")");
        }

        annonceRepository.delete(annonce);
        log.info("Annonce supprimée [id={}, par userId={}]", id, userId);
    }

    public Optional<Annonce> findById(Long id) {
        return annonceRepository.findById(id);
    }

    public Optional<Annonce> findByIdWithDetails(Long id) {
        return annonceRepository.findByIdWithDetails(id);
    }

    public Page<Annonce> findPublished(int page) {
        return findPublished(page, DEFAULT_PAGE_SIZE);
    }

    public Page<Annonce> findPublished(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return annonceRepository.findByStatusWithDetails(AnnonceStatus.PUBLISHED, pageable);
    }

    public Page<Annonce> findAll(int page) {
        return findAll(page, DEFAULT_PAGE_SIZE);
    }

    public Page<Annonce> findAll(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return annonceRepository.findAllWithDetails(pageable);
    }

    public Page<Annonce> search(AnnonceFilterDTO filter, Pageable pageable) {
        Specification<Annonce> spec = Specification.where(null);
        if (filter.getQ() != null && !filter.getQ().isEmpty()) {
            spec = spec.and(AnnonceSpecifications.hasKeyword(filter.getQ()));
        }
        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            try {
                AnnonceStatus status = AnnonceStatus.valueOf(filter.getStatus().toUpperCase());
                spec = spec.and(AnnonceSpecifications.hasStatus(status));
            } catch (IllegalArgumentException e) {
                log.warn("Statut invalide pour le filtre: {}", filter.getStatus());
            }
        }
        if (filter.getCategoryId() != null) {
            spec = spec.and(AnnonceSpecifications.hasCategoryId(filter.getCategoryId()));
        }
        if (filter.getAuthorId() != null) {
            spec = spec.and(AnnonceSpecifications.hasAuthorId(filter.getAuthorId()));
        }
        if (filter.getFromDate() != null || filter.getToDate() != null) {
            spec = spec.and(AnnonceSpecifications.createdBetween(filter.getFromDate(), filter.getToDate()));
        }

        return annonceRepository.findAll(spec, pageable);
    }

    public Page<Annonce> search(String keyword, int page) {
        return search(keyword, page, DEFAULT_PAGE_SIZE);
    }

    public Page<Annonce> search(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return annonceRepository.searchByKeyword(keyword, pageable);
    }

    public Page<Annonce> findByCategoryAndStatus(Long categoryId, AnnonceStatus status, int page) {
        return findByCategoryAndStatus(categoryId, status, page, DEFAULT_PAGE_SIZE);
    }

    public Page<Annonce> findByCategoryAndStatus(Long categoryId, AnnonceStatus status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return annonceRepository.findByCategoryIdAndStatusWithDetails(categoryId, status, pageable);
    }

    public List<Annonce> findByAuthor(Long authorId) {
        return annonceRepository.findByAuthorIdWithDetails(authorId);
    }

    public long countPublished() {
        return annonceRepository.countByStatus(AnnonceStatus.PUBLISHED);
    }

    public int getTotalPages() {
        return getTotalPages(DEFAULT_PAGE_SIZE);
    }

    public int getTotalPages(int pageSize) {
        long total = countPublished();
        return (int) Math.ceil((double) total / pageSize);
    }

    private void checkOwnership(Annonce annonce, Long userId) {
        if (annonce.getAuthor() == null || !annonce.getAuthor().getId().equals(userId)) {
            throw new SecurityException("Vous n'êtes pas l'auteur de cette annonce");
        }
    }
}

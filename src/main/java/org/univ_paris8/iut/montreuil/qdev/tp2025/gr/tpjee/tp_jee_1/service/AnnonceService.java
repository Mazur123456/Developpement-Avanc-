package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.InvalidStateException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.TransactionTemplate;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.persistence.OptimisticLockException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service métier pour la gestion des annonces.
 * Les transactions sont gérées via TransactionTemplate.
 * Délègue les opérations d'accès aux données au AnnonceRepository.
 */
public class AnnonceService {

    private static final Logger logger = LoggerFactory.getLogger(AnnonceService.class);
    private static final int DEFAULT_PAGE_SIZE = 10;
    private final AnnonceRepository annonceRepository = new AnnonceRepository();

    /**
     * Crée une nouvelle annonce (statut DRAFT par défaut)
     */
    public Annonce create(String title, String description, String adress, String mail,
            Long authorId, Long categoryId) throws ValidationUtil.ValidationException, EntityNotFoundException {
        return TransactionTemplate.executeInTransaction(em -> {
            Annonce annonce = new Annonce(title, description, adress, mail);

            // Associer l'auteur si fourni
            if (authorId != null) {
                User author = em.find(User.class, authorId);
                if (author != null) {
                    annonce.setAuthor(author);
                }
            }

            // Associer la catégorie si fournie
            if (categoryId != null) {
                Category category = em.find(Category.class, categoryId);
                if (category != null) {
                    annonce.setCategory(category);
                }
            }

            ValidationUtil.validateAndThrow(annonce);
            annonceRepository.create(em, annonce);
            logger.info("Annonce créée avec succès [id={}, titre={}, auteur={}]",
                    annonce.getId(), annonce.getTitle(), authorId);
            return annonce;
        });
    }

    /**
     * Met à jour une annonce existante (vérification de propriété)
     */
    public Annonce update(Long id, String title, String description, String adress,
            String mail, Long categoryId, Long userId)
            throws ValidationUtil.ValidationException, EntityNotFoundException {
        try {
            return TransactionTemplate.executeInTransaction(em -> {
                Annonce annonce = em.find(Annonce.class, id);
                if (annonce == null) {
                    throw new EntityNotFoundException("Annonce", id);
                }

                checkOwnership(annonce, userId);

                // Exercice 7.2 : Une annonce PUBLISHED ne peut plus être modifiée
                if (annonce.isPublished()) {
                    throw new InvalidStateException("Une annonce publiée ne peut plus être modifiée");
                }

                annonce.setTitle(title);
                annonce.setDescription(description);
                annonce.setAdress(adress);
                annonce.setMail(mail);

                if (categoryId != null) {
                    Category category = em.find(Category.class, categoryId);
                    annonce.setCategory(category);
                }

                ValidationUtil.validateAndThrow(annonce);
                logger.info("Annonce mise à jour [id={}, par userId={}]", id, userId);
                return annonce;
            });
        } catch (OptimisticLockException e) {
            throw new InvalidStateException(
                    "Conflit de concurrence : l'annonce a été modifiée par un autre utilisateur");
        }
    }

    /**
     * Publie une annonce (DRAFT → PUBLISHED) — vérification de propriété
     */
    public Annonce publish(Long id, Long userId) throws EntityNotFoundException, InvalidStateException {
        return TransactionTemplate.executeInTransaction(em -> {
            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new EntityNotFoundException("Annonce", id);
            }

            checkOwnership(annonce, userId);

            if (annonce.getStatus() != AnnonceStatus.DRAFT) {
                throw new InvalidStateException(annonce.getStatus().name(), AnnonceStatus.DRAFT.name());
            }

            annonce.publish();
            logger.info("Annonce publiée [id={}, par userId={}]", id, userId);
            return annonce;
        });
    }

    /**
     * Archive une annonce (PUBLISHED → ARCHIVED) — vérification de propriété
     */
    public Annonce archive(Long id, Long userId) throws EntityNotFoundException, InvalidStateException {
        return TransactionTemplate.executeInTransaction(em -> {
            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new EntityNotFoundException("Annonce", id);
            }

            checkOwnership(annonce, userId);

            if (annonce.getStatus() != AnnonceStatus.PUBLISHED) {
                throw new InvalidStateException(annonce.getStatus().name(), AnnonceStatus.PUBLISHED.name());
            }

            annonce.archive();
            logger.info("Annonce archivée [id={}, par userId={}]", id, userId);
            return annonce;
        });
    }

    /**
     * Supprime une annonce — vérification de propriété
     */
    public void delete(Long id, Long userId) throws EntityNotFoundException {
        TransactionTemplate.executeInTransactionVoid(em -> {
            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new EntityNotFoundException("Annonce", id);
            }

            checkOwnership(annonce, userId);

            // Exercice 7.3 : Archivage obligatoire avant suppression
            if (!annonce.isArchived()) {
                throw new InvalidStateException(
                        "L'annonce doit être archivée avant suppression (statut actuel: "
                                + annonce.getStatus().name() + ")");
            }

            em.remove(annonce);
            logger.info("Annonce supprimée [id={}, par userId={}]", id, userId);
        });
    }

    /**
     * Trouve une annonce par son ID
     */
    public Optional<Annonce> findById(Long id) {
        return TransactionTemplate.executeReadOnly(em -> annonceRepository.findById(em, id));
    }

    /**
     * Trouve une annonce avec ses détails (auteur et catégorie)
     */
    public Optional<Annonce> findByIdWithDetails(Long id) {
        return TransactionTemplate.executeReadOnly(em -> annonceRepository.findByIdWithDetails(em, id));
    }

    /**
     * Récupère toutes les annonces publiées avec pagination
     */
    public List<Annonce> findPublished(int page) {
        return findPublished(page, DEFAULT_PAGE_SIZE);
    }

    public List<Annonce> findPublished(int page, int pageSize) {
        return TransactionTemplate.executeReadOnly(em -> annonceRepository.findPublishedPaginated(em, page, pageSize));
    }

    /**
     * Récupère toutes les annonces avec pagination
     */
    public List<Annonce> findAll(int page) {
        return findAll(page, DEFAULT_PAGE_SIZE);
    }

    public List<Annonce> findAll(int page, int pageSize) {
        return TransactionTemplate.executeReadOnly(em -> annonceRepository.findAllPaginated(em, page, pageSize));
    }

    /**
     * Recherche par mot-clé avec pagination
     */
    public List<Annonce> search(String keyword, int page) {
        return search(keyword, page, DEFAULT_PAGE_SIZE);
    }

    public List<Annonce> search(String keyword, int page, int pageSize) {
        return TransactionTemplate
                .executeReadOnly(em -> annonceRepository.searchByKeywordPaginated(em, keyword, page, pageSize));
    }

    /**
     * Filtre par catégorie et statut avec pagination
     */
    public List<Annonce> findByCategoryAndStatus(Long categoryId, AnnonceStatus status, int page) {
        return findByCategoryAndStatus(categoryId, status, page, DEFAULT_PAGE_SIZE);
    }

    public List<Annonce> findByCategoryAndStatus(Long categoryId, AnnonceStatus status,
            int page, int pageSize) {
        return TransactionTemplate.executeReadOnly(
                em -> annonceRepository.findByCategoryAndStatus(em, categoryId, status, page, pageSize));
    }

    /**
     * Récupère les annonces d'un utilisateur
     */
    public List<Annonce> findByAuthor(Long authorId) {
        return TransactionTemplate.executeReadOnly(em -> annonceRepository.findByAuthor(em, authorId));
    }

    /**
     * Compte le nombre total d'annonces publiées
     */
    public long countPublished() {
        return TransactionTemplate.executeReadOnly(em -> annonceRepository.countPublished(em));
    }

    /**
     * Calcule le nombre de pages
     */
    public int getTotalPages() {
        return getTotalPages(DEFAULT_PAGE_SIZE);
    }

    public int getTotalPages(int pageSize) {
        long total = countPublished();
        return (int) Math.ceil((double) total / pageSize);
    }

    /**
     * Vérifie que l'utilisateur connecté est bien l'auteur de l'annonce
     */
    private void checkOwnership(Annonce annonce, Long userId) {
        if (annonce.getAuthor() == null || !annonce.getAuthor().getId().equals(userId)) {
            throw new SecurityException("Vous n'êtes pas l'auteur de cette annonce");
        }
    }
}

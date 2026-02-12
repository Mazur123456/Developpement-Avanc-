package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.InvalidStateException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.JPAUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des annonces.
 * Les transactions sont gérées ici, pas dans les Repositories ni les Servlets.
 * Délègue les opérations d'accès aux données au AnnonceRepository.
 */
public class AnnonceService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private final AnnonceRepository annonceRepository = new AnnonceRepository();

    /**
     * Crée une nouvelle annonce (statut DRAFT par défaut)
     */
    public Annonce create(String title, String description, String adress, String mail,
            Long authorId, Long categoryId) throws ValidationUtil.ValidationException, EntityNotFoundException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

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
            em.getTransaction().commit();
            return annonce;
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
     * Met à jour une annonce existante (vérification de propriété)
     */
    public Annonce update(Long id, String title, String description, String adress,
            String mail, Long categoryId, Long userId)
            throws ValidationUtil.ValidationException, EntityNotFoundException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new EntityNotFoundException("Annonce", id);
            }

            checkOwnership(annonce, userId);

            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);

            if (categoryId != null) {
                Category category = em.find(Category.class, categoryId);
                annonce.setCategory(category);
            }

            // Validation avant commit
            ValidationUtil.validateAndThrow(annonce);

            em.getTransaction().commit();
            return annonce;
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
     * Publie une annonce (DRAFT → PUBLISHED) — vérification de propriété
     */
    public Annonce publish(Long id, Long userId) throws EntityNotFoundException, InvalidStateException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new EntityNotFoundException("Annonce", id);
            }

            checkOwnership(annonce, userId);

            if (annonce.getStatus() != AnnonceStatus.DRAFT) {
                throw new InvalidStateException(annonce.getStatus().name(), AnnonceStatus.DRAFT.name());
            }

            annonce.publish();
            em.getTransaction().commit();
            return annonce;
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
     * Archive une annonce (PUBLISHED → ARCHIVED) — vérification de propriété
     */
    public Annonce archive(Long id, Long userId) throws EntityNotFoundException, InvalidStateException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new EntityNotFoundException("Annonce", id);
            }

            checkOwnership(annonce, userId);

            if (annonce.getStatus() != AnnonceStatus.PUBLISHED) {
                throw new InvalidStateException(annonce.getStatus().name(), AnnonceStatus.PUBLISHED.name());
            }

            annonce.archive();
            em.getTransaction().commit();
            return annonce;
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
     * Supprime une annonce — vérification de propriété
     */
    public void delete(Long id, Long userId) throws EntityNotFoundException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new EntityNotFoundException("Annonce", id);
            }

            checkOwnership(annonce, userId);

            em.remove(annonce);
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

    /**
     * Trouve une annonce par son ID
     */
    public Optional<Annonce> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return annonceRepository.findById(em, id);
        } finally {
            em.close();
        }
    }

    /**
     * Trouve une annonce avec ses détails (auteur et catégorie)
     */
    public Optional<Annonce> findByIdWithDetails(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return annonceRepository.findByIdWithDetails(em, id);
        } finally {
            em.close();
        }
    }

    /**
     * Récupère toutes les annonces publiées avec pagination
     */
    public List<Annonce> findPublished(int page) {
        return findPublished(page, DEFAULT_PAGE_SIZE);
    }

    public List<Annonce> findPublished(int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return annonceRepository.findPublishedPaginated(em, page, pageSize);
        } finally {
            em.close();
        }
    }

    /**
     * Récupère toutes les annonces avec pagination
     */
    public List<Annonce> findAll(int page) {
        return findAll(page, DEFAULT_PAGE_SIZE);
    }

    public List<Annonce> findAll(int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return annonceRepository.findAllPaginated(em, page, pageSize);
        } finally {
            em.close();
        }
    }

    /**
     * Recherche par mot-clé avec pagination
     */
    public List<Annonce> search(String keyword, int page) {
        return search(keyword, page, DEFAULT_PAGE_SIZE);
    }

    public List<Annonce> search(String keyword, int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return annonceRepository.searchByKeywordPaginated(em, keyword, page, pageSize);
        } finally {
            em.close();
        }
    }

    /**
     * Filtre par catégorie et statut avec pagination
     */
    public List<Annonce> findByCategoryAndStatus(Long categoryId, AnnonceStatus status, int page) {
        return findByCategoryAndStatus(categoryId, status, page, DEFAULT_PAGE_SIZE);
    }

    public List<Annonce> findByCategoryAndStatus(Long categoryId, AnnonceStatus status,
            int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return annonceRepository.findByCategoryAndStatus(em, categoryId, status, page, pageSize);
        } finally {
            em.close();
        }
    }

    /**
     * Récupère les annonces d'un utilisateur
     */
    public List<Annonce> findByAuthor(Long authorId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return annonceRepository.findByAuthor(em, authorId);
        } finally {
            em.close();
        }
    }

    /**
     * Compte le nombre total d'annonces publiées
     */
    public long countPublished() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return annonceRepository.countPublished(em);
        } finally {
            em.close();
        }
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

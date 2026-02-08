package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.JPAUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des annonces
 * Les transactions sont gérées ici, pas dans les Servlets
 */
public class AnnonceService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String PARAM_STATUS = "status";

    /**
     * Crée une nouvelle annonce (statut DRAFT par défaut)
     */
    public Annonce create(String title, String description, String adress, String mail,
            Long authorId, Long categoryId) throws ValidationUtil.ValidationException {
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
            em.persist(annonce);
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
     * Met à jour une annonce
     */
    public Annonce update(Long id, String title, String description, String adress,
            String mail, Long categoryId) throws ValidationUtil.ValidationException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new IllegalArgumentException("Annonce non trouvée");
            }

            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);

            if (categoryId != null) {
                Category category = em.find(Category.class, categoryId);
                annonce.setCategory(category);
            }

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
     * Publie une annonce (DRAFT → PUBLISHED)
     */
    public Annonce publish(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new IllegalArgumentException("Annonce non trouvée");
            }

            if (annonce.getStatus() != AnnonceStatus.DRAFT) {
                throw new IllegalStateException("Seules les annonces en brouillon peuvent être publiées");
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
     * Archive une annonce (PUBLISHED → ARCHIVED)
     */
    public Annonce archive(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = em.find(Annonce.class, id);
            if (annonce == null) {
                throw new IllegalArgumentException("Annonce non trouvée");
            }

            if (annonce.getStatus() != AnnonceStatus.PUBLISHED) {
                throw new IllegalStateException("Seules les annonces publiées peuvent être archivées");
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
     * Supprime une annonce
     */
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Annonce annonce = em.find(Annonce.class, id);
            if (annonce != null) {
                em.remove(annonce);
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

    /**
     * Trouve une annonce par son ID
     */
    public Optional<Annonce> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Annonce annonce = em.find(Annonce.class, id);
            return Optional.ofNullable(annonce);
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
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "LEFT JOIN FETCH a.author " +
                            "LEFT JOIN FETCH a.category " +
                            "WHERE a.id = :id",
                    Annonce.class);
            query.setParameter("id", id);
            List<Annonce> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
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
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "WHERE a.status = :" + PARAM_STATUS + " " +
                            "ORDER BY a.date DESC",
                    Annonce.class);
            query.setParameter(PARAM_STATUS, AnnonceStatus.PUBLISHED);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
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
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a ORDER BY a.date DESC", Annonce.class);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
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
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "WHERE LOWER(a.title) LIKE LOWER(:keyword) " +
                            "OR LOWER(a.description) LIKE LOWER(:keyword) " +
                            "ORDER BY a.date DESC",
                    Annonce.class);
            query.setParameter("keyword", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
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
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "WHERE a.category.id = :categoryId " +
                            "AND a.status = :" + PARAM_STATUS + " " +
                            "ORDER BY a.date DESC",
                    Annonce.class);
            query.setParameter("categoryId", categoryId);
            query.setParameter(PARAM_STATUS, status);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
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
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "WHERE a.author.id = :authorId " +
                            "ORDER BY a.date DESC",
                    Annonce.class);
            query.setParameter("authorId", authorId);
            return query.getResultList();
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
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM Annonce a WHERE a.status = :" + PARAM_STATUS, Long.class);
            query.setParameter(PARAM_STATUS, AnnonceStatus.PUBLISHED);
            return query.getSingleResult();
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
}

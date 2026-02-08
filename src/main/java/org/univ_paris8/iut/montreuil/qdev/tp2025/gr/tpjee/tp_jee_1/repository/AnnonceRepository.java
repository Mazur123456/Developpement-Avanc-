package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.JPAUtil;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour la gestion des annonces
 * Utilise exclusivement JPQL (pas de JDBC)
 */
public class AnnonceRepository {

    /**
     * Crée une nouvelle annonce
     */
    public Annonce create(Annonce annonce) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
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
     * Trouve une annonce par son ID avec fetch des relations
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
     * Récupère toutes les annonces
     */
    public List<Annonce> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a ORDER BY a.date DESC", Annonce.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Récupère les annonces avec pagination
     */
    public List<Annonce> findAllPaginated(int page, int pageSize) {
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
     * Recherche par mot-clé dans le titre ou la description
     */
    public List<Annonce> searchByKeyword(String keyword) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "WHERE LOWER(a.title) LIKE LOWER(:keyword) " +
                            "OR LOWER(a.description) LIKE LOWER(:keyword) " +
                            "ORDER BY a.date DESC",
                    Annonce.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Recherche par mot-clé avec pagination
     */
    public List<Annonce> searchByKeywordPaginated(String keyword, int page, int pageSize) {
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
     * Filtre par catégorie
     */
    public List<Annonce> findByCategory(Long categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "WHERE a.category.id = :categoryId " +
                            "ORDER BY a.date DESC",
                    Annonce.class);
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Filtre par statut
     */
    public List<Annonce> findByStatus(AnnonceStatus status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "WHERE a.status = :status " +
                            "ORDER BY a.date DESC",
                    Annonce.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Filtre par catégorie ET statut avec pagination
     */
    public List<Annonce> findByCategoryAndStatus(Long categoryId, AnnonceStatus status, int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "WHERE a.category.id = :categoryId " +
                            "AND a.status = :status " +
                            "ORDER BY a.date DESC",
                    Annonce.class);
            query.setParameter("categoryId", categoryId);
            query.setParameter("status", status);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Récupère les annonces publiées avec pagination
     */
    public List<Annonce> findPublishedPaginated(int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                    "SELECT a FROM Annonce a " +
                            "WHERE a.status = :status " +
                            "ORDER BY a.date DESC",
                    Annonce.class);
            query.setParameter("status", AnnonceStatus.PUBLISHED);
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
     * Compte le nombre total d'annonces
     */
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM Annonce a", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Compte le nombre d'annonces publiées
     */
    public long countPublished() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM Annonce a WHERE a.status = :status", Long.class);
            query.setParameter("status", AnnonceStatus.PUBLISHED);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Met à jour une annonce
     */
    public Annonce update(Annonce annonce) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Annonce updated = em.merge(annonce);
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
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour la gestion des annonces.
 * Utilise exclusivement JPQL (pas de JDBC).
 * Les transactions sont gérées par la couche Service, pas ici.
 */
public class AnnonceRepository {

    /**
     * Persiste une nouvelle annonce
     */
    public Annonce create(EntityManager em, Annonce annonce) {
        em.persist(annonce);
        return annonce;
    }

    /**
     * Trouve une annonce par son ID
     */
    public Optional<Annonce> findById(EntityManager em, Long id) {
        Annonce annonce = em.find(Annonce.class, id);
        return Optional.ofNullable(annonce);
    }

    /**
     * Trouve une annonce par son ID avec fetch des relations (auteur et catégorie)
     */
    public Optional<Annonce> findByIdWithDetails(EntityManager em, Long id) {
        TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a " +
                        "LEFT JOIN FETCH a.author " +
                        "LEFT JOIN FETCH a.category " +
                        "WHERE a.id = :id",
                Annonce.class);
        query.setParameter("id", id);
        List<Annonce> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Récupère toutes les annonces
     */
    public List<Annonce> findAll(EntityManager em) {
        TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a ORDER BY a.date DESC", Annonce.class);
        return query.getResultList();
    }

    /**
     * Récupère les annonces avec pagination
     */
    public List<Annonce> findAllPaginated(EntityManager em, int page, int pageSize) {
        TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a ORDER BY a.date DESC", Annonce.class);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    /**
     * Recherche par mot-clé dans le titre ou la description
     */
    public List<Annonce> searchByKeyword(EntityManager em, String keyword) {
        TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a " +
                        "WHERE LOWER(a.title) LIKE LOWER(:keyword) " +
                        "OR LOWER(a.description) LIKE LOWER(:keyword) " +
                        "ORDER BY a.date DESC",
                Annonce.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    /**
     * Recherche par mot-clé avec pagination
     */
    public List<Annonce> searchByKeywordPaginated(EntityManager em, String keyword, int page, int pageSize) {
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
    }

    /**
     * Filtre par catégorie
     */
    public List<Annonce> findByCategory(EntityManager em, Long categoryId) {
        TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a " +
                        "WHERE a.category.id = :categoryId " +
                        "ORDER BY a.date DESC",
                Annonce.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }

    /**
     * Filtre par statut
     */
    public List<Annonce> findByStatus(EntityManager em, AnnonceStatus status) {
        TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a " +
                        "WHERE a.status = :status " +
                        "ORDER BY a.date DESC",
                Annonce.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    /**
     * Filtre par catégorie ET statut avec pagination
     */
    public List<Annonce> findByCategoryAndStatus(EntityManager em, Long categoryId, AnnonceStatus status,
            int page, int pageSize) {
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
    }

    /**
     * Récupère les annonces publiées avec pagination
     */
    public List<Annonce> findPublishedPaginated(EntityManager em, int page, int pageSize) {
        TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a " +
                        "WHERE a.status = :status " +
                        "ORDER BY a.date DESC",
                Annonce.class);
        query.setParameter("status", AnnonceStatus.PUBLISHED);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    /**
     * Récupère les annonces d'un utilisateur
     */
    public List<Annonce> findByAuthor(EntityManager em, Long authorId) {
        TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a " +
                        "WHERE a.author.id = :authorId " +
                        "ORDER BY a.date DESC",
                Annonce.class);
        query.setParameter("authorId", authorId);
        return query.getResultList();
    }

    /**
     * Compte le nombre total d'annonces
     */
    public long count(EntityManager em) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Annonce a", Long.class);
        return query.getSingleResult();
    }

    /**
     * Compte le nombre d'annonces publiées
     */
    public long countPublished(EntityManager em) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Annonce a WHERE a.status = :status", Long.class);
        query.setParameter("status", AnnonceStatus.PUBLISHED);
        return query.getSingleResult();
    }

    /**
     * Met à jour une annonce (merge)
     */
    public Annonce update(EntityManager em, Annonce annonce) {
        return em.merge(annonce);
    }

    /**
     * Supprime une annonce
     */
    public void delete(EntityManager em, Long id) {
        Annonce annonce = em.find(Annonce.class, id);
        if (annonce != null) {
            em.remove(annonce);
        }
    }
}

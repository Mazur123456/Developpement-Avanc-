package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA pour la gestion des utilisateurs.
 * Les transactions sont gérées par la couche Service, pas ici.
 */
public class UserRepository {

    /**
     * Persiste un nouvel utilisateur
     */
    public User create(EntityManager em, User user) {
        em.persist(user);
        return user;
    }

    /**
     * Trouve un utilisateur par son ID
     */
    public Optional<User> findById(EntityManager em, Long id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }

    /**
     * Trouve un utilisateur par son username
     */
    public Optional<User> findByUsername(EntityManager em, String username) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Trouve un utilisateur par son email
     */
    public Optional<User> findByEmail(EntityManager em, String email) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Vérifie si un username existe déjà
     */
    public boolean existsByUsername(EntityManager em, String username) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }

    /**
     * Vérifie si un email existe déjà
     */
    public boolean existsByEmail(EntityManager em, String email) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<User> findAll(EntityManager em) {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u ORDER BY u.createdAt DESC", User.class);
        return query.getResultList();
    }

    /**
     * Met à jour un utilisateur (merge)
     */
    public User update(EntityManager em, User user) {
        return em.merge(user);
    }

    /**
     * Supprime un utilisateur
     */
    public void delete(EntityManager em, Long id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        }
    }
}

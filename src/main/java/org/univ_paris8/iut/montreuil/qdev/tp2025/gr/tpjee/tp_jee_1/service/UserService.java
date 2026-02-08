package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.JPAUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des utilisateurs
 * Les transactions sont gérées ici, pas dans les Servlets
 */
public class UserService {

    private static final String PARAM_USERNAME = "username";

    /**
     * Inscrit un nouvel utilisateur
     */
    public User register(String username, String email, String password) throws ValidationUtil.ValidationException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Vérifier si le username existe déjà
            if (existsByUsername(em, username)) {
                throw new IllegalArgumentException("Ce nom d'utilisateur existe déjà");
            }

            // Vérifier si l'email existe déjà
            if (existsByEmail(em, email)) {
                throw new IllegalArgumentException("Cet email est déjà utilisé");
            }

            User user = new User(username, email, password);
            ValidationUtil.validateAndThrow(user);

            em.persist(user);
            em.getTransaction().commit();
            return user;
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
     * Authentifie un utilisateur
     */
    public Optional<User> authenticate(String username, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :" + PARAM_USERNAME + " AND u.password = :password",
                    User.class);
            query.setParameter(PARAM_USERNAME, username);
            query.setParameter("password", password);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Trouve un utilisateur par son ID
     */
    public Optional<User> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            User user = em.find(User.class, id);
            return Optional.ofNullable(user);
        } finally {
            em.close();
        }
    }

    /**
     * Trouve un utilisateur par son username
     */
    public Optional<User> findByUsername(String username) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :" + PARAM_USERNAME, User.class);
            query.setParameter(PARAM_USERNAME, username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<User> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u ORDER BY u.createdAt DESC", User.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Met à jour un utilisateur
     */
    public User update(User user) throws ValidationUtil.ValidationException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User updated = em.merge(user);
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
     * Supprime un utilisateur
     */
    public void delete(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
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

    // Méthodes utilitaires privées
    private boolean existsByUsername(EntityManager em, String username) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :" + PARAM_USERNAME, Long.class);
        query.setParameter(PARAM_USERNAME, username);
        return query.getSingleResult() > 0;
    }

    private boolean existsByEmail(EntityManager em, String email) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() > 0;
    }
}

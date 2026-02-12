package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.DuplicateEntityException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.UserRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.JPAUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.PasswordUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des utilisateurs.
 * Les transactions sont gérées ici, pas dans les Repositories ni les Servlets.
 * Les mots de passe sont hachés avec SHA-256 avant stockage.
 */
public class UserService {

    private final UserRepository userRepository = new UserRepository();

    /**
     * Inscrit un nouvel utilisateur (mot de passe haché)
     */
    public User register(String username, String email, String password)
            throws ValidationUtil.ValidationException, DuplicateEntityException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            // Vérifier si le username existe déjà
            if (userRepository.existsByUsername(em, username)) {
                throw new DuplicateEntityException("username", username);
            }

            // Vérifier si l'email existe déjà
            if (userRepository.existsByEmail(em, email)) {
                throw new DuplicateEntityException("email", email);
            }

            // Hacher le mot de passe avant stockage
            String hashedPassword = PasswordUtil.hashPassword(password);
            User user = new User(username, email, hashedPassword);
            ValidationUtil.validateAndThrow(user);

            userRepository.create(em, user);
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
     * Authentifie un utilisateur en vérifiant le hash du mot de passe
     */
    public Optional<User> authenticate(String username, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Optional<User> userOpt = userRepository.findByUsername(em, username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (PasswordUtil.checkPassword(password, user.getPassword())) {
                    return Optional.of(user);
                }
            }
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
            return userRepository.findById(em, id);
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
            return userRepository.findByUsername(em, username);
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
            return userRepository.findAll(em);
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
            ValidationUtil.validateAndThrow(user);
            User updated = userRepository.update(em, user);
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
            userRepository.delete(em, id);
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

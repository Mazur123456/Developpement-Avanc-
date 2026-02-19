package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.DuplicateEntityException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.UserRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.PasswordUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.TransactionTemplate;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.ValidationUtil;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service métier pour la gestion des utilisateurs.
 * Les transactions sont gérées via TransactionTemplate.
 * Les mots de passe sont hachés avec SHA-256 avant stockage.
 */
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository = new UserRepository();

    /**
     * Inscrit un nouvel utilisateur (mot de passe haché)
     */
    public User register(String username, String email, String password)
            throws ValidationUtil.ValidationException, DuplicateEntityException {
        return TransactionTemplate.executeInTransaction(em -> {
            if (userRepository.existsByUsername(em, username)) {
                throw new DuplicateEntityException("username", username);
            }

            if (userRepository.existsByEmail(em, email)) {
                throw new DuplicateEntityException("email", email);
            }

            String hashedPassword = PasswordUtil.hashPassword(password);
            User user = new User(username, email, hashedPassword);
            ValidationUtil.validateAndThrow(user);

            userRepository.create(em, user);
            logger.info("Utilisateur inscrit [id={}, username={}]", user.getId(), username);
            return user;
        });
    }

    /**
     * Authentifie un utilisateur en vérifiant le hash du mot de passe
     */
    public Optional<User> authenticate(String username, String password) {
        return TransactionTemplate.executeReadOnly(em -> {
            Optional<User> userOpt = userRepository.findByUsername(em, username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (PasswordUtil.checkPassword(password, user.getPassword())) {
                    logger.info("Authentification réussie [username={}]", username);
                    return Optional.of(user);
                }
            }
            logger.warn("Échec d'authentification [username={}]", username);
            return Optional.empty();
        });
    }

    /**
     * Trouve un utilisateur par son ID
     */
    public Optional<User> findById(Long id) {
        return TransactionTemplate.executeReadOnly(em -> userRepository.findById(em, id));
    }

    /**
     * Trouve un utilisateur par son username
     */
    public Optional<User> findByUsername(String username) {
        return TransactionTemplate.executeReadOnly(em -> userRepository.findByUsername(em, username));
    }

    /**
     * Récupère tous les utilisateurs
     */
    public List<User> findAll() {
        return TransactionTemplate.executeReadOnly(em -> userRepository.findAll(em));
    }

    /**
     * Met à jour un utilisateur
     */
    public User update(User user) throws ValidationUtil.ValidationException {
        return TransactionTemplate.executeInTransaction(em -> {
            ValidationUtil.validateAndThrow(user);
            User updated = userRepository.update(em, user);
            logger.info("Utilisateur mis à jour [id={}]", user.getId());
            return updated;
        });
    }

    /**
     * Supprime un utilisateur
     */
    public void delete(Long id) {
        TransactionTemplate.executeInTransactionVoid(em -> {
            userRepository.delete(em, id);
            logger.info("Utilisateur supprimé [id={}]", id);
        });
    }
}

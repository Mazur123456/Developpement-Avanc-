package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.UserRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.JPAUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.PasswordUtil;

import javax.persistence.EntityManager;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de gestion de l'authentification (Token-based).
 * Stockage en mémoire des tokens actifs (Exercice 5).
 */
public class AuthService {

    private final UserRepository userRepository = new UserRepository();

    // Stockage en mémoire des tokens : Token -> User
    // ConcurrentHashMap pour thread-safety
    private static final Map<String, User> activeTokens = new ConcurrentHashMap<>();

    /**
     * Authentifie l'utilisateur et retourne un token si succès.
     * 
     * @return Le token généré ou null si échec.
     */
    public String login(String username, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Optional<User> userOpt = userRepository.findByUsername(em, username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (PasswordUtil.checkPassword(password, user.getPassword())) {
                    String token = UUID.randomUUID().toString();
                    activeTokens.put(token, user);
                    return token;
                }
            }
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Valide un token et retourne l'utilisateur associé.
     */
    public Optional<User> validateToken(String token) {
        if (token == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(activeTokens.get(token));
    }

    /**
     * Invalide un token (logout).
     */
    public void logout(String token) {
        if (token != null) {
            activeTokens.remove(token);
        }
    }
}

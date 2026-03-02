package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.DuplicateEntityException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.UserRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.PasswordUtil;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User register(String username, String email, String password) throws DuplicateEntityException {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateEntityException("username", username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEntityException("email", email);
        }

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(username, email, hashedPassword);

        user = userRepository.save(user);
        log.info("Utilisateur inscrit [id={}, username={}]", user.getId(), username);
        return user;
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtil.checkPassword(password, user.getPassword())) {
                log.info("Authentification réussie [username={}]", username);
                return Optional.of(user);
            }
        }
        log.warn("Échec d'authentification [username={}]", username);
        return Optional.empty();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User update(User user) {
        User updated = userRepository.save(user);
        log.info("Utilisateur mis à jour [id={}]", user.getId());
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("Utilisateur supprimé [id={}]", id);
    }
}

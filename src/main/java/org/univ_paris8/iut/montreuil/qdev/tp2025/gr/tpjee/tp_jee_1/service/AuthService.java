package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private static final Map<String, User> activeTokens = new ConcurrentHashMap<>();

    public String login(String username, String password) {
        Optional<User> userOpt = userService.authenticate(username, password);
        if (userOpt.isPresent()) {
            String token = UUID.randomUUID().toString();
            activeTokens.put(token, userOpt.get());
            log.info("Login réussi [username={}]", username);
            return token;
        }
        log.warn("Échec de login [username={}]", username);
        return null;
    }

    public Optional<User> validateToken(String token) {
        if (token == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(activeTokens.get(token));
    }

    public void logout(String token) {
        if (token != null) {
            activeTokens.remove(token);
        }
    }
}

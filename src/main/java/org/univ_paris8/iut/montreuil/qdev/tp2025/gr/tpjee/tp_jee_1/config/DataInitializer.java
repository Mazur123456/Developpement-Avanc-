package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Vérification des données initiales...");

        if (!userRepository.existsByEmail("admin@test.com")) {
            log.info("Création de l'utilisateur de test: admin@test.com");
            User admin = User.builder()
                    .username("admin")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("Admin1234!"))
                    .build();
            userRepository.save(admin);
            log.info("Utilisateur admin créé avec succès avec le mot de passe 'Admin1234!'.");
        } else {
            // Update the password of existing admin to Admin1234! if it isn't matching
            userRepository.findByEmail("admin@test.com").ifPresent(admin -> {
                admin.setPassword(passwordEncoder.encode("Admin1234!"));
                userRepository.save(admin);
            });
            log.info("L'utilisateur de test existe déjà, mot de passe forcé à 'Admin1234!'.");
        }
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.UserRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Vérification des données initiales...");

        // Creation of ADMIN User
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
            userRepository.findByEmail("admin@test.com").ifPresent(admin -> {
                admin.setPassword(passwordEncoder.encode("Admin1234!"));
                userRepository.save(admin);
            });
            log.info("L'utilisateur admin existe déjà, mot de passe forcé à 'Admin1234!'.");
        }

        // Creation of STANDARD User
        if (!userRepository.existsByEmail("user@user.com")) {
            log.info("Création de l'utilisateur standard: user@user.com");
            User normalUser = User.builder()
                    .username("user")
                    .email("user@user.com")
                    .password(passwordEncoder.encode("password"))
                    .build();
            userRepository.save(normalUser);
            log.info("Utilisateur standard créé avec succès.");
        }

        // Creation of Categories
        if (categoryRepository.count() == 0) {
            log.info("Création des catégories par défaut...");
            List<String> defaultCategories = List.of(
                    "Immobilier", "Véhicules", "Emploi", "Informatique",
                    "Mode", "Maison", "Loisirs", "Autres");

            for (String label : defaultCategories) {
                Category cat = Category.builder().label(label).build();
                categoryRepository.save(cat);
            }
            log.info("{} catégories créées avec succès.", defaultCategories.size());
        }
    }
}

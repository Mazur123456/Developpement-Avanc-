package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceCreateDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.LoginDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.UserRepository;

import java.util.List;

import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration MockMvc + Testcontainers.
 *
 * IMPORTANT : MockMvc avec @SpringBootTest n'applique PAS automatiquement le
 * server.servlet.context-path dans les appels perform(). On surcharge le
 * context-path
 * à "/" dans @DynamicPropertySource pour que les chemins dans les tests
 * correspondent
 * directement aux @RequestMapping des controllers (ex: /auth/login, /annonces).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AnnonceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        // Désactiver le context-path pour les tests MockMvc
        // Les chemins des tests correspondent directement aux @RequestMapping (sans
        // /api)
        registry.add("server.servlet.context-path", () -> "/");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    private String adminToken;
    private String userToken;
    private Long categoryId;

    @BeforeEach
    void setup() throws Exception {
        // DataInitializer injecte : admin@test.com / Admin1234! et user@user.com /
        // password

        // 1. Token admin
        LoginDTO adminLogin = new LoginDTO();
        adminLogin.setEmail("admin@test.com");
        adminLogin.setPassword("Admin1234!");
        MvcResult adminResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andReturn();
        if (adminResult.getResponse().getStatus() == 200) {
            String content = adminResult.getResponse().getContentAsString();
            adminToken = JsonPath.read(content, "$.token");
        }

        // 2. Token user
        LoginDTO userLogin = new LoginDTO();
        userLogin.setEmail("user@user.com");
        userLogin.setPassword("password");
        MvcResult userResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLogin)))
                .andReturn();
        if (userResult.getResponse().getStatus() == 200) {
            String content = userResult.getResponse().getContentAsString();
            userToken = JsonPath.read(content, "$.token");
        }

        // 3. Préparer une catégorie pour les tests de création
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            Category newCat = Category.builder().label("Test Category").build();
            categoryRepository.save(newCat);
            categoryId = newCat.getId();
        } else {
            categoryId = categories.get(0).getId();
        }
    }

    @Test
    void testLoginValidCredentials_Returns200AndToken() throws Exception {
        LoginDTO loginDto = new LoginDTO();
        loginDto.setEmail("admin@test.com");
        loginDto.setPassword("Admin1234!");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.roles").exists());
    }

    @Test
    void testGetAnnoncesWithoutToken_Returns401() throws Exception {
        mockMvc.perform(get("/annonces"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAnnoncesWithInvalidToken_Returns401() throws Exception {
        mockMvc.perform(get("/annonces")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAnnoncesWithValidToken_Returns200() throws Exception {
        mockMvc.perform(get("/annonces")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateAnnonceWithRoleUser_Returns201() throws Exception {
        AnnonceCreateDTO createDTO = new AnnonceCreateDTO();
        createDTO.setTitle("Test Title");
        createDTO.setDescription("Test Description");
        createDTO.setAdress("Paris");
        createDTO.setMail("user@user.com");
        createDTO.setCategoryId(categoryId);

        mockMvc.perform(post("/annonces")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    void testDeleteAnnonceWithRoleUser_Returns403() throws Exception {
        User adminUser = userRepository.findByEmail("admin@test.com").get();
        Category cat = categoryRepository.findById(categoryId).get();
        Annonce adminAnnonce = annonceRepository.save(
                Annonce.builder()
                        .title("Admin Annonce")
                        .description("Desc")
                        .author(adminUser)
                        .category(cat)
                        .status(AnnonceStatus.ARCHIVED)
                        .build());

        mockMvc.perform(delete("/annonces/" + adminAnnonce.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteAnnonceWithRoleAdmin_Returns204() throws Exception {
        User adminUser = userRepository.findByEmail("admin@test.com").get();
        Category cat = categoryRepository.findById(categoryId).get();
        Annonce adminAnnonce = annonceRepository.save(
                Annonce.builder()
                        .title("Admin Annonce to delete")
                        .description("Desc")
                        .author(adminUser)
                        .category(cat)
                        .status(AnnonceStatus.ARCHIVED)
                        .build());

        mockMvc.perform(delete("/annonces/" + adminAnnonce.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isNoContent()); // HTTP 204
    }
}

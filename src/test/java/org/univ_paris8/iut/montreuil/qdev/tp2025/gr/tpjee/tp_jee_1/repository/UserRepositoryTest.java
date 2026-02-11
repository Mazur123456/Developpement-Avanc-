package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository;

import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Niveau 1 – Tests d'intégration du UserRepository
 */
class UserRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private UserRepository userRepository;

    @BeforeAll
    static void setUpFactory() {
        emf = Persistence.createEntityManagerFactory("TestPU");
    }

    @AfterAll
    static void tearDownFactory() {
        if (emf != null)
            emf.close();
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        userRepository = new UserRepository();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Annonce").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("Créer un utilisateur")
    void testCreate() {
        em.getTransaction().begin();
        User user = new User("newuser", "new@test.com", "hash123");
        userRepository.create(em, user);
        em.getTransaction().commit();

        assertNotNull(user.getId());
        assertEquals("newuser", user.getUsername());
    }

    @Test
    @DisplayName("Trouver par username")
    void testFindByUsername() {
        em.getTransaction().begin();
        User user = new User("findme", "find@test.com", "hash");
        userRepository.create(em, user);
        em.getTransaction().commit();

        Optional<User> found = userRepository.findByUsername(em, "findme");
        assertTrue(found.isPresent());
        assertEquals("find@test.com", found.get().getEmail());
    }

    @Test
    @DisplayName("findByUsername retourne empty pour username inexistant")
    void testFindByUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername(em, "nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("existsByUsername détecte les doublons")
    void testExistsByUsername() {
        em.getTransaction().begin();
        User user = new User("duplicate", "dup@test.com", "hash");
        userRepository.create(em, user);
        em.getTransaction().commit();

        assertTrue(userRepository.existsByUsername(em, "duplicate"));
        assertFalse(userRepository.existsByUsername(em, "unique"));
    }

    @Test
    @DisplayName("existsByEmail détecte les doublons")
    void testExistsByEmail() {
        em.getTransaction().begin();
        User user = new User("emailtest", "exists@test.com", "hash");
        userRepository.create(em, user);
        em.getTransaction().commit();

        assertTrue(userRepository.existsByEmail(em, "exists@test.com"));
        assertFalse(userRepository.existsByEmail(em, "nope@test.com"));
    }

    @Test
    @DisplayName("Lister tous les utilisateurs")
    void testFindAll() {
        em.getTransaction().begin();
        userRepository.create(em, new User("user1", "u1@test.com", "h1"));
        userRepository.create(em, new User("user2", "u2@test.com", "h2"));
        em.getTransaction().commit();

        List<User> all = userRepository.findAll(em);
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Supprimer un utilisateur")
    void testDelete() {
        em.getTransaction().begin();
        User user = new User("toDelete", "del@test.com", "hash");
        userRepository.create(em, user);
        em.getTransaction().commit();

        Long id = user.getId();

        em.getTransaction().begin();
        userRepository.delete(em, id);
        em.getTransaction().commit();

        Optional<User> found = userRepository.findById(em, id);
        assertFalse(found.isPresent());
    }
}

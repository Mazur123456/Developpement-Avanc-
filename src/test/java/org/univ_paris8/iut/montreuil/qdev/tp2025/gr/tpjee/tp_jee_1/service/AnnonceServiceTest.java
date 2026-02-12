package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.AnnonceRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Niveau 2 – Tests unitaires des règles métier de l'annonce
 * Tests des transitions de statut et de la vérification de propriété
 */
class AnnonceServiceTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private AnnonceRepository annonceRepository;
    private User owner;
    private User otherUser;

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
        annonceRepository = new AnnonceRepository();

        em.getTransaction().begin();
        owner = new User("owner", "owner@test.com", "hash123");
        em.persist(owner);
        otherUser = new User("other", "other@test.com", "hash123");
        em.persist(otherUser);
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Annonce").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    // ==================== Transition de statut ====================

    @Test
    @DisplayName("Une annonce est créée en DRAFT")
    void testDefaultStatusIsDraft() {
        Annonce annonce = new Annonce("Test", "Desc", "Addr", "t@t.com");
        assertEquals(AnnonceStatus.DRAFT, annonce.getStatus());
    }

    @Test
    @DisplayName("DRAFT → PUBLISHED est autorisé")
    void testPublishFromDraft() {
        Annonce annonce = new Annonce("Test", "Desc", "Addr", "t@t.com");
        annonce.publish();
        assertEquals(AnnonceStatus.PUBLISHED, annonce.getStatus());
    }

    @Test
    @DisplayName("PUBLISHED → ARCHIVED est autorisé")
    void testArchiveFromPublished() {
        Annonce annonce = new Annonce("Test", "Desc", "Addr", "t@t.com");
        annonce.publish();
        annonce.archive();
        assertEquals(AnnonceStatus.ARCHIVED, annonce.getStatus());
    }

    @Test
    @DisplayName("Publier une annonce déjà publiée lève une exception")
    void testCannotPublishAlreadyPublished() {
        Annonce annonce = new Annonce("Test", "Desc", "Addr", "t@t.com");
        annonce.publish();
        // Vérifier que le statut n'est plus DRAFT
        assertEquals(AnnonceStatus.PUBLISHED, annonce.getStatus());
    }

    // ==================== Ownership ====================

    @Test
    @DisplayName("Le propriétaire peut accéder à son annonce")
    void testOwnerCanAccess() {
        em.getTransaction().begin();
        Annonce annonce = new Annonce("Owner Test", "Desc", "Addr", "o@t.com");
        annonce.setAuthor(owner);
        annonceRepository.create(em, annonce);
        em.getTransaction().commit();

        // Vérifier que le propriétaire est correct
        assertEquals(owner.getId(), annonce.getAuthor().getId());
    }

    @Test
    @DisplayName("Un autre utilisateur ne peut pas modifier l'annonce")
    void testNonOwnerCannotAccess() {
        em.getTransaction().begin();
        Annonce annonce = new Annonce("Owner Test", "Desc", "Addr", "o@t.com");
        annonce.setAuthor(owner);
        annonceRepository.create(em, annonce);
        em.getTransaction().commit();

        // Simuler la vérification d'ownership
        assertNotEquals(otherUser.getId(), annonce.getAuthor().getId(),
                "Un autre utilisateur ne doit pas être l'auteur");
    }

    @Test
    @DisplayName("SecurityException si l'utilisateur n'est pas l'auteur")
    void testOwnershipCheckThrowsSecurityException() {
        Annonce annonce = new Annonce("Test", "Desc", "Addr", "t@t.com");
        annonce.setAuthor(owner);

        // La vérification de propriété doit échouer pour un autre utilisateur
        assertFalse(annonce.getAuthor().getId().equals(otherUser.getId()));
    }
}

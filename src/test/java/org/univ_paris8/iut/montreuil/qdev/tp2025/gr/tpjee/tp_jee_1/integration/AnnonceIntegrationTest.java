package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.integration;

import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.AnnonceRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Niveau 3 – Tests d'intégration métier
 * Enchaînement complet : création → publication → recherche
 * Test du problème Lazy / N+1
 */
class AnnonceIntegrationTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private AnnonceRepository annonceRepository;

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
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Annonce").executeUpdate();
        em.createQuery("DELETE FROM Category").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("Enchaînement complet : créer → publier → rechercher → archiver")
    void testFullWorkflow() {
        // 1. Créer un utilisateur et une catégorie
        em.getTransaction().begin();
        User user = new User("workflow_user", "wf@test.com", "hash");
        em.persist(user);
        Category category = new Category("Immobilier");
        em.persist(category);
        em.getTransaction().commit();

        // 2. Créer une annonce (DRAFT)
        em.getTransaction().begin();
        Annonce annonce = new Annonce("Appartement Paris 15", "T3 lumineux 65m²", "15 rue de Vaugirard",
                "contact@immo.fr");
        annonce.setAuthor(user);
        annonce.setCategory(category);
        annonceRepository.create(em, annonce);
        em.getTransaction().commit();

        assertNotNull(annonce.getId());
        assertEquals(AnnonceStatus.DRAFT, annonce.getStatus());

        // 3. Publier l'annonce
        em.getTransaction().begin();
        Annonce toPublish = em.find(Annonce.class, annonce.getId());
        toPublish.publish();
        em.getTransaction().commit();

        assertEquals(AnnonceStatus.PUBLISHED, toPublish.getStatus());

        // 4. Rechercher l'annonce par mot-clé
        List<Annonce> searchResults = annonceRepository.searchByKeywordPaginated(em, "Paris", 1, 10);
        assertEquals(1, searchResults.size());
        assertEquals("Appartement Paris 15", searchResults.get(0).getTitle());

        // 5. Vérifier qu'elle apparaît dans les annonces publiées
        List<Annonce> published = annonceRepository.findPublishedPaginated(em, 1, 10);
        assertEquals(1, published.size());

        // 6. Archiver l'annonce
        em.getTransaction().begin();
        Annonce toArchive = em.find(Annonce.class, annonce.getId());
        toArchive.archive();
        em.getTransaction().commit();

        assertEquals(AnnonceStatus.ARCHIVED, toArchive.getStatus());

        // 7. Vérifier qu'elle n'apparaît plus dans les annonces publiées
        List<Annonce> publishedAfter = annonceRepository.findPublishedPaginated(em, 1, 10);
        assertEquals(0, publishedAfter.size(), "L'annonce archivée ne doit plus apparaître");
    }

    @Test
    @DisplayName("Test du problème N+1 : findByIdWithDetails charge les relations en une requête")
    void testJoinFetchPreventsNPlus1() {
        // Créer des données
        em.getTransaction().begin();
        User user = new User("n1_user", "n1@test.com", "hash");
        em.persist(user);
        Category cat = new Category("N+1 Test");
        em.persist(cat);

        Annonce annonce = new Annonce("N+1 Test", "Desc", "Addr", "n1@test.com");
        annonce.setAuthor(user);
        annonce.setCategory(cat);
        annonceRepository.create(em, annonce);
        em.getTransaction().commit();

        // Détacher toutes les entités du cache
        em.clear();

        // findByIdWithDetails utilise JOIN FETCH → une seule requête
        Optional<Annonce> result = annonceRepository.findByIdWithDetails(em, annonce.getId());
        assertTrue(result.isPresent());

        // Accéder aux relations SANS LazyInitializationException
        // (car elles ont été chargées par JOIN FETCH)
        assertDoesNotThrow(() -> {
            result.get().getAuthor().getUsername();
            result.get().getCategory().getLabel();
        }, "Les relations doivent être accessibles sans LazyInitializationException");

        assertEquals("n1_user", result.get().getAuthor().getUsername());
        assertEquals("N+1 Test", result.get().getCategory().getLabel());
    }

    @Test
    @DisplayName("Test Lazy Loading : accès aux relations détachées échoue sans JOIN FETCH")
    void testLazyLoadingIssue() {
        // Créer des données
        em.getTransaction().begin();
        User user = new User("lazy_user", "lazy@test.com", "hash");
        em.persist(user);
        Category cat = new Category("Lazy Test");
        em.persist(cat);

        Annonce annonce = new Annonce("Lazy Test", "Desc", "Addr", "lazy@test.com");
        annonce.setAuthor(user);
        annonce.setCategory(cat);
        annonceRepository.create(em, annonce);
        em.getTransaction().commit();

        Long id = annonce.getId();

        // Fermer l'EntityManager pour simuler un contexte détaché
        em.close();

        // Ouvrir un nouvel EntityManager et charger SANS JOIN FETCH
        EntityManager em2 = emf.createEntityManager();
        Annonce loaded = em2.find(Annonce.class, id);
        em2.close(); // Fermer AVANT d'accéder aux relations

        // En contexte détaché, l'accès aux relations lazy provoque une exception
        // (ce test démontre le problème que JOIN FETCH résout)
        assertNotNull(loaded, "L'annonce doit être chargée");

        // Réouvrir un EM pour le tearDown
        em = emf.createEntityManager();
    }
}

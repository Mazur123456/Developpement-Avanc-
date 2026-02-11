package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository;

import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Niveau 1 – Tests d'intégration du AnnonceRepository
 * Utilise H2 en mémoire (TestPU)
 */
class AnnonceRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private AnnonceRepository annonceRepository;
    private User testUser;
    private Category testCategory;

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

        // Créer les données de test
        em.getTransaction().begin();
        testUser = new User("testuser", "test@test.com", "hashedpass");
        em.persist(testUser);
        testCategory = new Category("Test Category");
        em.persist(testCategory);
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        // Nettoyer les données
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Annonce").executeUpdate();
        em.createQuery("DELETE FROM Category").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    // ==================== CRUD Tests ====================

    @Test
    @DisplayName("Créer une annonce et vérifier sa persistance")
    void testCreate() {
        em.getTransaction().begin();
        Annonce annonce = new Annonce("Titre Test", "Description", "Adresse", "test@mail.com");
        annonce.setAuthor(testUser);
        annonce.setCategory(testCategory);
        annonceRepository.create(em, annonce);
        em.getTransaction().commit();

        assertNotNull(annonce.getId(), "L'ID doit être généré après persist");
        assertEquals(AnnonceStatus.DRAFT, annonce.getStatus(), "Le statut par défaut doit être DRAFT");
    }

    @Test
    @DisplayName("Trouver une annonce par ID")
    void testFindById() {
        em.getTransaction().begin();
        Annonce annonce = new Annonce("Find Test", "Desc", "Addr", "find@test.com");
        annonce.setAuthor(testUser);
        annonceRepository.create(em, annonce);
        em.getTransaction().commit();

        Optional<Annonce> found = annonceRepository.findById(em, annonce.getId());
        assertTrue(found.isPresent(), "L'annonce doit être trouvée");
        assertEquals("Find Test", found.get().getTitle());
    }

    @Test
    @DisplayName("findById retourne Optional.empty pour un ID inexistant")
    void testFindByIdNotFound() {
        Optional<Annonce> found = annonceRepository.findById(em, 99999L);
        assertFalse(found.isPresent(), "Aucune annonce ne doit être trouvée");
    }

    @Test
    @DisplayName("Trouver par ID avec détails (JOIN FETCH)")
    void testFindByIdWithDetails() {
        em.getTransaction().begin();
        Annonce annonce = new Annonce("Detail Test", "Desc", "Addr", "detail@test.com");
        annonce.setAuthor(testUser);
        annonce.setCategory(testCategory);
        annonceRepository.create(em, annonce);
        em.getTransaction().commit();

        em.clear(); // Détacher pour forcer le reload

        Optional<Annonce> found = annonceRepository.findByIdWithDetails(em, annonce.getId());
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getAuthor().getUsername());
        assertEquals("Test Category", found.get().getCategory().getLabel());
    }

    @Test
    @DisplayName("Supprimer une annonce")
    void testDelete() {
        em.getTransaction().begin();
        Annonce annonce = new Annonce("Delete Test", "Desc", "Addr", "del@test.com");
        annonce.setAuthor(testUser);
        annonceRepository.create(em, annonce);
        em.getTransaction().commit();

        Long id = annonce.getId();

        em.getTransaction().begin();
        annonceRepository.delete(em, id);
        em.getTransaction().commit();

        Optional<Annonce> found = annonceRepository.findById(em, id);
        assertFalse(found.isPresent(), "L'annonce supprimée ne doit plus exister");
    }

    // ==================== Search & Pagination ====================

    @Test
    @DisplayName("Recherche par mot-clé dans le titre")
    void testSearchByKeyword() {
        em.getTransaction().begin();
        Annonce a1 = new Annonce("Appartement Paris", "Bel appart", "Paris", "a@b.com");
        a1.setAuthor(testUser);
        a1.publish(); // publier pour apparaître dans les résultats
        annonceRepository.create(em, a1);

        Annonce a2 = new Annonce("Maison Lyon", "Grande maison", "Lyon", "c@d.com");
        a2.setAuthor(testUser);
        a2.publish();
        annonceRepository.create(em, a2);
        em.getTransaction().commit();

        List<Annonce> results = annonceRepository.searchByKeywordPaginated(em, "Paris", 1, 10);
        assertEquals(1, results.size());
        assertEquals("Appartement Paris", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Pagination des annonces publiées")
    void testPagination() {
        em.getTransaction().begin();
        for (int i = 0; i < 15; i++) {
            Annonce a = new Annonce("Annonce " + i, "Desc " + i, "Addr", "p" + i + "@test.com");
            a.setAuthor(testUser);
            a.publish();
            annonceRepository.create(em, a);
        }
        em.getTransaction().commit();

        List<Annonce> page1 = annonceRepository.findPublishedPaginated(em, 1, 10);
        List<Annonce> page2 = annonceRepository.findPublishedPaginated(em, 2, 10);

        assertEquals(10, page1.size(), "Page 1 doit contenir 10 éléments");
        assertEquals(5, page2.size(), "Page 2 doit contenir 5 éléments");
    }

    @Test
    @DisplayName("Comptage des annonces publiées")
    void testCountPublished() {
        em.getTransaction().begin();
        Annonce a1 = new Annonce("Publiée", "Desc", "Addr", "p@t.com");
        a1.setAuthor(testUser);
        a1.publish();
        annonceRepository.create(em, a1);

        Annonce a2 = new Annonce("Brouillon", "Desc", "Addr", "b@t.com");
        a2.setAuthor(testUser);
        annonceRepository.create(em, a2);
        em.getTransaction().commit();

        long count = annonceRepository.countPublished(em);
        assertEquals(1, count, "Seules les annonces PUBLISHED sont comptées");
    }

    @Test
    @DisplayName("Filtre par catégorie et statut")
    void testFilterByCategoryAndStatus() {
        em.getTransaction().begin();
        Annonce a1 = new Annonce("Cat Test", "Desc", "Addr", "ct@t.com");
        a1.setAuthor(testUser);
        a1.setCategory(testCategory);
        a1.publish();
        annonceRepository.create(em, a1);

        Annonce a2 = new Annonce("Sans Cat", "Desc", "Addr", "sc@t.com");
        a2.setAuthor(testUser);
        a2.publish();
        annonceRepository.create(em, a2);
        em.getTransaction().commit();

        List<Annonce> results = annonceRepository.findByCategoryAndStatus(
                em, testCategory.getId(), AnnonceStatus.PUBLISHED, 1, 10);
        assertEquals(1, results.size());
        assertEquals("Cat Test", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Lister les annonces d'un auteur")
    void testFindByAuthor() {
        em.getTransaction().begin();
        Annonce a1 = new Annonce("Mon annonce", "Desc", "Addr", "my@t.com");
        a1.setAuthor(testUser);
        annonceRepository.create(em, a1);
        em.getTransaction().commit();

        List<Annonce> results = annonceRepository.findByAuthor(em, testUser.getId());
        assertEquals(1, results.size());
    }
}

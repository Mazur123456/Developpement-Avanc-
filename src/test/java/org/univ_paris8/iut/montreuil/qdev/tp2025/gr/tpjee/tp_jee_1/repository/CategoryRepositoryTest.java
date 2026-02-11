package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository;

import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Niveau 1 – Tests d'intégration du CategoryRepository
 */
class CategoryRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private CategoryRepository categoryRepository;

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
        categoryRepository = new CategoryRepository();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Annonce").executeUpdate();
        em.createQuery("DELETE FROM Category").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @DisplayName("Créer une catégorie")
    void testCreate() {
        em.getTransaction().begin();
        Category cat = new Category("Immobilier");
        categoryRepository.create(em, cat);
        em.getTransaction().commit();

        assertNotNull(cat.getId());
        assertEquals("Immobilier", cat.getLabel());
    }

    @Test
    @DisplayName("Trouver par ID")
    void testFindById() {
        em.getTransaction().begin();
        Category cat = new Category("Véhicules");
        categoryRepository.create(em, cat);
        em.getTransaction().commit();

        Optional<Category> found = categoryRepository.findById(em, cat.getId());
        assertTrue(found.isPresent());
        assertEquals("Véhicules", found.get().getLabel());
    }

    @Test
    @DisplayName("existsByLabel détecte les doublons")
    void testExistsByLabel() {
        em.getTransaction().begin();
        categoryRepository.create(em, new Category("Emploi"));
        em.getTransaction().commit();

        assertTrue(categoryRepository.existsByLabel(em, "Emploi"));
        assertFalse(categoryRepository.existsByLabel(em, "Services"));
    }

    @Test
    @DisplayName("Lister toutes les catégories")
    void testFindAll() {
        em.getTransaction().begin();
        categoryRepository.create(em, new Category("Cat1"));
        categoryRepository.create(em, new Category("Cat2"));
        categoryRepository.create(em, new Category("Cat3"));
        em.getTransaction().commit();

        List<Category> all = categoryRepository.findAll(em);
        assertEquals(3, all.size());
    }

    @Test
    @DisplayName("Supprimer une catégorie")
    void testDelete() {
        em.getTransaction().begin();
        Category cat = new Category("ToDelete");
        categoryRepository.create(em, cat);
        em.getTransaction().commit();

        Long id = cat.getId();

        em.getTransaction().begin();
        categoryRepository.delete(em, id);
        em.getTransaction().commit();

        Optional<Category> found = categoryRepository.findById(em, id);
        assertFalse(found.isPresent());
    }
}

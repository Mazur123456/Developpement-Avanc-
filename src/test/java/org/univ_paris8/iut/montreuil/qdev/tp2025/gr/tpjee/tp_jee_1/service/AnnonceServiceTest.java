package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.InvalidStateException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnnonceServiceTest {

    @Mock
    private AnnonceRepository annonceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private AnnonceService annonceService;

    private User author;
    private Category category;
    private Annonce annonce;

    @BeforeEach
    void setUp() {
        author = User.builder().id(1L).username("testuser").email("test@user.com").build();
        category = Category.builder().id(10L).label("IT").build();
        annonce = Annonce.builder()
                .id(100L)
                .title("Titre")
                .description("Desc")
                .author(author)
                .category(category)
                .status(AnnonceStatus.DRAFT)
                .build();
    }

    @Test
    void findAll_ShouldReturnPagedList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Annonce> page = new PageImpl<>(List.of(annonce));
        when(annonceRepository.findAllWithDetails(pageable)).thenReturn(page);

        Page<Annonce> result = annonceService.findAll(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(annonceRepository, times(1)).findAllWithDetails(pageable);
    }

    @Test
    void findById_WhenFound_ShouldReturnAnnonce() {
        when(annonceRepository.findByIdWithDetails(100L)).thenReturn(Optional.of(annonce));

        Optional<Annonce> result = annonceService.findByIdWithDetails(100L);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getId());
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        when(annonceRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

        Optional<Annonce> result = annonceService.findByIdWithDetails(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void create_ShouldSaveAndReturnAnnonce() throws EntityNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(annonceRepository.save(any(Annonce.class))).thenReturn(annonce);

        Annonce created = annonceService.create("Titre", "Desc", "Paris", "test@mail.com", 1L, 10L);

        assertNotNull(created);
        verify(annonceRepository, times(1)).save(any(Annonce.class));
    }

    @Test
    void update_WhenAuthorMatches_ShouldUpdate() throws EntityNotFoundException {
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(annonceRepository.save(any(Annonce.class))).thenReturn(annonce);

        Annonce updated = annonceService.update(100L, "New Titre", "New Desc", "New Adress", "new@mail.com", 10L, 1L);

        assertNotNull(updated);
        verify(annonceRepository, times(1)).save(any(Annonce.class));
    }

    @Test
    void update_WhenAuthorDiffers_ShouldThrowSecurityException() {
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

        // Attempting to update with different userId (2L instead of author's 1L)
        assertThrows(SecurityException.class,
                () -> annonceService.update(100L, "New Titre", "New Desc", "New Adress", "new@mail.com", 10L, 2L));

        verify(annonceRepository, never()).save(any(Annonce.class));
    }

    @Test
    void delete_WhenAuthorMatchesAndArchived_ShouldDelete() throws EntityNotFoundException {
        annonce.setStatus(AnnonceStatus.ARCHIVED); // Must be archived to delete cleanly in current logic
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

        annonceService.delete(100L, 1L);

        verify(annonceRepository, times(1)).delete(annonce);
    }

    @Test
    void delete_WhenNotArchived_ShouldThrowInvalidStateException() {
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

        assertThrows(InvalidStateException.class, () -> annonceService.delete(100L, 1L));

        verify(annonceRepository, never()).delete(any(Annonce.class));
    }

    @Test
    void delete_WhenAuthorDiffers_ShouldThrowSecurityException() {
        when(annonceRepository.findById(100L)).thenReturn(Optional.of(annonce));

        assertThrows(SecurityException.class, () -> annonceService.delete(100L, 2L));

        verify(annonceRepository, never()).delete(any(Annonce.class));
    }
}

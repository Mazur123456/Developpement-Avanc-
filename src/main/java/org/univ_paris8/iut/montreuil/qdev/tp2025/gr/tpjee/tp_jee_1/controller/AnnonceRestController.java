package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.mapper.AnnonceMapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/annonces")
@RequiredArgsConstructor
@Tag(name = "Annonces API", description = "CRUD operations for Annonces")
public class AnnonceRestController {

    private final AnnonceService annonceService;
    private final AuthService authService;
    private final AnnonceMapper annonceMapper;

    @GetMapping
    @Operation(summary = "Get all published annonces with pagination")
    public ResponseEntity<List<AnnonceDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Annonce> annonces = annonceService.findPublished(page, size);
        return ResponseEntity.ok(annonceMapper.toDTOList(annonces.getContent()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get annonce by ID")
    public ResponseEntity<AnnonceDTO> getById(@PathVariable Long id) {
        Annonce annonce = annonceService.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce", id));
        return ResponseEntity.ok(annonceMapper.toDTO(annonce));
    }

    @PostMapping
    @Operation(summary = "Create a new annonce")
    public ResponseEntity<AnnonceDTO> create(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AnnonceDTO dto) {
        User user = getAuthenticatedUser(authHeader);

        Annonce created = annonceService.create(
                dto.getTitle(),
                dto.getDescription(),
                dto.getAdress(),
                dto.getMail(),
                user.getId(),
                dto.getCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(annonceMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing annonce")
    public ResponseEntity<AnnonceDTO> update(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id, @Valid @RequestBody AnnonceDTO dto) {
        User user = getAuthenticatedUser(authHeader);

        Annonce updated = annonceService.update(
                id,
                dto.getTitle(),
                dto.getDescription(),
                dto.getAdress(),
                dto.getMail(),
                dto.getCategoryId(),
                user.getId());
        return ResponseEntity.ok(annonceMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an annonce")
    public ResponseEntity<Void> delete(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id) {
        User user = getAuthenticatedUser(authHeader);
        annonceService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an annonce")
    public ResponseEntity<AnnonceDTO> partialUpdate(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long id, @RequestBody AnnonceDTO dto) {
        User user = getAuthenticatedUser(authHeader);

        Annonce existing = annonceService.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce", id));

        String title = dto.getTitle() != null ? dto.getTitle() : existing.getTitle();
        String description = dto.getDescription() != null ? dto.getDescription() : existing.getDescription();
        String adress = dto.getAdress() != null ? dto.getAdress() : existing.getAdress();
        String mail = dto.getMail() != null ? dto.getMail() : existing.getMail();
        Long categoryId = dto.getCategoryId() != null ? dto.getCategoryId()
                : (existing.getCategory() != null ? existing.getCategory().getId() : null);

        Annonce updated = annonceService.update(id, title, description, adress, mail, categoryId, user.getId());
        return ResponseEntity.ok(annonceMapper.toDTO(updated));
    }

    private User getAuthenticatedUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new org.springframework.security.access.AccessDeniedException("Utilisateur non authentifié");
        }
        String token = authHeader.substring(7);
        return authService.validateToken(token)
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("Token invalide"));
    }
}

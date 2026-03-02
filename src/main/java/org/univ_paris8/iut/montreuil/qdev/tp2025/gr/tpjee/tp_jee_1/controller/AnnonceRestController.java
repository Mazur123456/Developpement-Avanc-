package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceCreateDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceFilterDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceUpdateDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.mapper.AnnonceMapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/annonces")
@RequiredArgsConstructor
@Tag(name = "Annonces API", description = "CRUD operations for Annonces")
public class AnnonceRestController {

    private final AnnonceService annonceService;
    private final UserService userService;
    private final AnnonceMapper annonceMapper;

    @GetMapping
    @Operation(summary = "Get all annonces with pagination and optional filters")
    public ResponseEntity<Page<AnnonceDTO>> getAll(
            @ModelAttribute AnnonceFilterDTO filter,
            Pageable pageable) {

        Page<Annonce> annonces = annonceService.search(filter, pageable);
        Page<AnnonceDTO> dtoPage = annonces.map(annonceMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
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
            @Valid @RequestBody AnnonceCreateDTO dto) {
        User user = getAuthenticatedUser();

        Annonce created = annonceService.create(
                dto.getTitle(),
                dto.getDescription(),
                dto.getAdress(),
                dto.getMail(),
                user.getId(),
                dto.getCategoryId());

        AnnonceDTO responseDto = annonceMapper.toDTO(created);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update completely an existing annonce")
    public ResponseEntity<AnnonceDTO> update(
            @PathVariable Long id, @Valid @RequestBody AnnonceUpdateDTO dto) {
        User user = getAuthenticatedUser();

        Annonce updated = annonceService.update(
                id,
                dto.getTitle(),
                dto.getDescription(),
                dto.getAdress(),
                dto.getMail(),
                dto.getCategoryId(),
                user.getId());

        // (Note: La mise a jour du status complet n'est pas couverte par la methode
        // update actuelle du service, elle sera geree plus tard ou via un mapper
        // custom)

        return ResponseEntity.ok(annonceMapper.toDTO(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an annonce")
    public ResponseEntity<AnnonceDTO> partialUpdate(
            @PathVariable Long id, @RequestBody AnnonceUpdateDTO dto) {
        User user = getAuthenticatedUser();

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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an annonce")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User user = getAuthenticatedUser();
        annonceService.delete(id, user.getId());
        return ResponseEntity.noContent().build(); // HTTP 204
    }

    private User getAuthenticatedUser() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new org.springframework.security.access.AccessDeniedException("Utilisateur non authentifié");
        }
        org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.security.CustomUserDetails userDetails = (org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.security.CustomUserDetails) auth
                .getPrincipal();

        return userService.findById(userDetails.getId())
                .orElseThrow(
                        () -> new org.springframework.security.access.AccessDeniedException("Utilisateur introuvable"));
    }
}

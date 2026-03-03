package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/annonces")
@RequiredArgsConstructor
@Tag(name = "Annonces API", description = "CRUD operations for Annonces")
public class AnnonceRestController {

    private final AnnonceService annonceService;
    private final UserService userService;
    private final AnnonceMapper annonceMapper;

    /** Champs triables valides, calculés par réflexion sur l'entité Annonce. */
    private static final Set<String> SORTABLE_FIELDS = Arrays.stream(Annonce.class.getDeclaredFields())
            .map(java.lang.reflect.Field::getName)
            .collect(Collectors.toSet());

    @GetMapping
    @Operation(summary = "Get all annonces with pagination")
    @ApiResponse(responseCode = "200", description = "List of annonces retrieved")
    @ApiResponse(responseCode = "400", description = "Invalid sort field")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Page<AnnonceDTO>> getAll(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        validateSort(pageable);
        Page<Annonce> annonces = annonceService.findAll(pageable.getPageNumber() + 1, pageable.getPageSize());
        Page<AnnonceDTO> dtoPage = annonces.map(annonceMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/search")
    @Operation(summary = "Search annonces with dynamic filters and pagination")
    @ApiResponse(responseCode = "200", description = "List of filtered annonces retrieved")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<Page<AnnonceDTO>> search(
            @Parameter(description = "Filters for annonces (q, status, categoryId, etc.)") @ModelAttribute AnnonceFilterDTO filter,
            @Parameter(description = "Pagination parameters") Pageable pageable) {

        Page<Annonce> annonces = annonceService.search(filter, pageable);
        Page<AnnonceDTO> dtoPage = annonces.map(annonceMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get annonce by ID")
    @ApiResponse(responseCode = "200", description = "Annonce found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Annonce not found")
    public ResponseEntity<AnnonceDTO> getById(
            @Parameter(description = "ID of the annonce") @PathVariable Long id) {
        Annonce annonce = annonceService.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce", id));
        return ResponseEntity.ok(annonceMapper.toDTO(annonce));
    }

    @PostMapping
    @Operation(summary = "Create a new annonce")
    @ApiResponse(responseCode = "201", description = "Annonce created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<AnnonceDTO> create(
            @Parameter(description = "Details of the new annonce") @Valid @RequestBody AnnonceCreateDTO dto) {
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
    @ApiResponse(responseCode = "200", description = "Annonce updated")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Not the author")
    @ApiResponse(responseCode = "404", description = "Annonce not found")
    public ResponseEntity<AnnonceDTO> update(
            @Parameter(description = "ID of the annonce") @PathVariable Long id,
            @Parameter(description = "New details") @Valid @RequestBody AnnonceUpdateDTO dto) {
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
    @ApiResponse(responseCode = "200", description = "Annonce updated")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Not the author")
    @ApiResponse(responseCode = "404", description = "Annonce not found")
    public ResponseEntity<AnnonceDTO> partialUpdate(
            @Parameter(description = "ID of the annonce") @PathVariable Long id,
            @Parameter(description = "Fields to update") @RequestBody AnnonceUpdateDTO dto) {
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
    @ApiResponse(responseCode = "204", description = "Annonce deleted")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN only")
    @ApiResponse(responseCode = "404", description = "Annonce not found")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the annonce") @PathVariable Long id) {
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

    /**
     * Valide que les propriétés de tri de la requête correspondent
     * à des champs réels de l'entité Annonce (via réflexion Java).
     * Lève une IllegalArgumentException (→ HTTP 400) si un champ inconnu est
     * fourni.
     */
    private void validateSort(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            String property = order.getProperty();
            if (!SORTABLE_FIELDS.contains(property)) {
                throw new IllegalArgumentException(
                        "Champ de tri invalide : '" + property + "'. Champs autorisés : " + SORTABLE_FIELDS);
            }
        });
    }
}

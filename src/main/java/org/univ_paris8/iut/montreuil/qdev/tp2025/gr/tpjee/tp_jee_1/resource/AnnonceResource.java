package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.resource;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.mapper.AnnonceMapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

/**
 * Ressource JAX-RS pour les annonces.
 * Expose les endpoints CRUD sous /api/annonces.
 *
 * IMPORTANT : Pas de logique métier ici !
 * La ressource appelle le service, qui contient la logique.
 */
@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnnonceResource {

    private final AnnonceService annonceService = new AnnonceService();

    /**
     * GET /api/annonces?page=1&size=10
     * Retourne la liste paginée des annonces.
     */
    @GET
    public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        List<Annonce> annonces = annonceService.findAll(page, size);
        List<AnnonceDTO> dtos = AnnonceMapper.toDTOList(annonces);
        return Response.ok(dtos).build();
    }

    /**
     * GET /api/annonces/{id}
     * Retourne le détail d'une annonce.
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Optional<Annonce> annonce = annonceService.findByIdWithDetails(id);
        if (annonce.isEmpty()) {
            throw new EntityNotFoundException("Annonce", id);
        }
        AnnonceDTO dto = AnnonceMapper.toDTO(annonce.get());
        return Response.ok(dto).build();
    }

    /**
     * POST /api/annonces
     * Crée une nouvelle annonce.
     * Retourne 201 Created avec l'annonce créée.
     */
    @POST
    public Response create(@Valid AnnonceDTO dto) {
        // Pour l'instant, authorId vient du DTO
        // À l'exercice 6, il viendra du token d'authentification
        Annonce created = annonceService.create(
                dto.getTitle(),
                dto.getDescription(),
                dto.getAdress(),
                dto.getMail(),
                dto.getAuthorId(),
                dto.getCategoryId());
        AnnonceDTO result = AnnonceMapper.toDTO(created);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * PUT /api/annonces/{id}
     * Met à jour une annonce existante.
     */
    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid AnnonceDTO dto) {
        // Pour l'instant, userId vient du DTO (authorId)
        // À l'exercice 6, il viendra du token d'authentification
        Annonce updated = annonceService.update(
                id,
                dto.getTitle(),
                dto.getDescription(),
                dto.getAdress(),
                dto.getMail(),
                dto.getCategoryId(),
                dto.getAuthorId());
        AnnonceDTO result = AnnonceMapper.toDTO(updated);
        return Response.ok(result).build();
    }

    /**
     * DELETE /api/annonces/{id}
     * Supprime une annonce.
     * Retourne 204 No Content.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id,
            @QueryParam("userId") Long userId) {
        // Pour l'instant, userId vient du QueryParam
        // À l'exercice 6, il viendra du token d'authentification
        annonceService.delete(id, userId);
        return Response.noContent().build();
    }

    /**
     * PATCH /api/annonces/{id}
     * Mise à jour partielle d'une annonce (Bonus).
     * Seuls les champs non-null dans le body seront mis à jour.
     */
    @PATCH
    @Path("/{id}")
    public Response partialUpdate(@PathParam("id") Long id, AnnonceDTO dto) {
        Optional<Annonce> existing = annonceService.findByIdWithDetails(id);
        if (existing.isEmpty()) {
            throw new EntityNotFoundException("Annonce", id);
        }

        Annonce annonce = existing.get();

        // Mise à jour partielle : on ne modifie que les champs fournis (non null)
        String title = dto.getTitle() != null ? dto.getTitle() : annonce.getTitle();
        String description = dto.getDescription() != null ? dto.getDescription() : annonce.getDescription();
        String adress = dto.getAdress() != null ? dto.getAdress() : annonce.getAdress();
        String mail = dto.getMail() != null ? dto.getMail() : annonce.getMail();
        Long categoryId = dto.getCategoryId() != null ? dto.getCategoryId()
                : (annonce.getCategory() != null ? annonce.getCategory().getId() : null);

        // Utilise l'authorId du DTO pour vérifier l'ownership
        // À l'exercice 6, il viendra du token
        Long userId = dto.getAuthorId() != null ? dto.getAuthorId()
                : (annonce.getAuthor() != null ? annonce.getAuthor().getId() : null);

        Annonce updated = annonceService.update(id, title, description, adress, mail, categoryId, userId);
        AnnonceDTO result = AnnonceMapper.toDTO(updated);
        return Response.ok(result).build();
    }
}

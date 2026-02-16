package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.resource;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.filter.TokenSecurityContext;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.mapper.AnnonceMapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service.AnnonceService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Optional;

/**
 * Ressource JAX-RS pour les annonces.
 * Expose les endpoints CRUD sous /api/annonces.
 * Utilise le SecurityContext pour récupérer l'utilisateur connecté.
 */
@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnnonceResource {

    private final AnnonceService annonceService = new AnnonceService();

    @Context
    SecurityContext securityContext;

    /**
     * GET /api/annonces?page=1&size=10
     * Retourne la liste des annonces (publiques ? ou toutes si admin ?).
     * Supposons liste des annonces publiques pour tout le monde.
     */
    @GET
    public Response getAll(@QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        // En mode public, on ne montre que les publiées ?
        // Le sujet ne précise pas, mais c'est logique.
        List<Annonce> annonces = annonceService.findPublished(page, size);
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
     */
    @POST
    public Response create(@Valid AnnonceDTO dto) {
        User authenticatedUser = getAuthenticatedUser();

        Annonce created = annonceService.create(
                dto.getTitle(),
                dto.getDescription(),
                dto.getAdress(),
                dto.getMail(),
                authenticatedUser.getId(), // Utilise l'ID du token
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
        User authenticatedUser = getAuthenticatedUser();

        Annonce updated = annonceService.update(
                id,
                dto.getTitle(),
                dto.getDescription(),
                dto.getAdress(),
                dto.getMail(),
                dto.getCategoryId(),
                authenticatedUser.getId()); // Vérification ownership via service
        AnnonceDTO result = AnnonceMapper.toDTO(updated);
        return Response.ok(result).build();
    }

    /**
     * DELETE /api/annonces/{id}
     * Supprime une annonce.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        User authenticatedUser = getAuthenticatedUser();
        annonceService.delete(id, authenticatedUser.getId()); // Vérification ownership via service
        return Response.noContent().build();
    }

    /**
     * PATCH /api/annonces/{id}
     * Mise à jour partielle d'une annonce (Bonus).
     */
    @PATCH
    @Path("/{id}")
    public Response partialUpdate(@PathParam("id") Long id, AnnonceDTO dto) {
        User authenticatedUser = getAuthenticatedUser();

        Optional<Annonce> existing = annonceService.findByIdWithDetails(id);
        if (existing.isEmpty()) {
            throw new EntityNotFoundException("Annonce", id);
        }

        Annonce annonce = existing.get();

        String title = dto.getTitle() != null ? dto.getTitle() : annonce.getTitle();
        String description = dto.getDescription() != null ? dto.getDescription() : annonce.getDescription();
        String adress = dto.getAdress() != null ? dto.getAdress() : annonce.getAdress();
        String mail = dto.getMail() != null ? dto.getMail() : annonce.getMail();
        Long categoryId = dto.getCategoryId() != null ? dto.getCategoryId()
                : (annonce.getCategory() != null ? annonce.getCategory().getId() : null);

        Annonce updated = annonceService.update(id, title, description, adress, mail, categoryId,
                authenticatedUser.getId());
        AnnonceDTO result = AnnonceMapper.toDTO(updated);
        return Response.ok(result).build();
    }

    private User getAuthenticatedUser() {
        if (securityContext != null && securityContext instanceof TokenSecurityContext) {
            return ((TokenSecurityContext) securityContext).getUser();
        }
        // Fallback ou erreur si pas de contexte (ne devrait pas arriver si le filtre
        // fait le job)
        // Mais pour GET public, on peut être ici sans auth ? Non, create/update/delete
        // sont filtrés.
        // Si on arrive ici sans Auth alors que le filtre l'exige, c'est un bug serveur
        // 500.
        throw new NotAuthorizedException("Utilisateur non authentifié");
    }
}

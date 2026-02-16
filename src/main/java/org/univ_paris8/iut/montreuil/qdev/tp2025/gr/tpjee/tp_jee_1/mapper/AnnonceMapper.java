package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.mapper;

import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre Annonce (Entity JPA) et AnnonceDTO.
 *
 * Pourquoi un Mapper ?
 * - Les entités JPA contiennent des relations lazy, des proxies Hibernate
 * - On ne veut pas exposer les mots de passe utilisateur, les IDs internes,
 * etc.
 * - Le DTO est un objet "plat" facile à sérialiser en JSON
 */
public class AnnonceMapper {

    /**
     * Convertit une entité Annonce en AnnonceDTO.
     * Utilisé pour les réponses (Entity → JSON)
     */
    public static AnnonceDTO toDTO(Annonce entity) {
        if (entity == null) {
            return null;
        }

        AnnonceDTO dto = new AnnonceDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setAdress(entity.getAdress());
        dto.setMail(entity.getMail());
        dto.setDate(entity.getDate());

        if (entity.getStatus() != null) {
            dto.setStatus(entity.getStatus().name());
        }

        // Mapper l'auteur (seulement id + username, PAS le mot de passe)
        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
            dto.setAuthorUsername(entity.getAuthor().getUsername());
        }

        // Mapper la catégorie
        if (entity.getCategory() != null) {
            dto.setCategoryId(entity.getCategory().getId());
            dto.setCategoryLabel(entity.getCategory().getLabel());
        }

        return dto;
    }

    /**
     * Convertit une liste d'entités en liste de DTOs.
     */
    public static List<AnnonceDTO> toDTOList(List<Annonce> entities) {
        return entities.stream()
                .map(AnnonceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit un AnnonceDTO en entité Annonce.
     * Utilisé pour la création (JSON → Entity)
     * Note : les relations (author, category) sont résolues dans le Service.
     */
    public static Annonce toEntity(AnnonceDTO dto) {
        if (dto == null) {
            return null;
        }

        Annonce entity = new Annonce();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setAdress(dto.getAdress());
        entity.setMail(dto.getMail());
        return entity;
    }
}

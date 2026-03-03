package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.UserDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.User;

/**
 * MapStruct mapper for converting between User entities and UserDTO.
 * Conversion automatique générée à la compilation.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convertit une entité User en UserDTO.
     * Le mot de passe est explicitement ignoré pour éviter son exposition via
     * l'API.
     */
    @Mapping(target = "password", ignore = true)
    UserDTO toDTO(User user);

    /**
     * Convertit un UserDTO en entité User.
     * Utile pour les créations / mises à jour de compte.
     */
    User toEntity(UserDTO dto);
}

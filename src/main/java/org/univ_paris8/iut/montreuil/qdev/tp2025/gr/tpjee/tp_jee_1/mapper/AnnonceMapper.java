package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceCreateDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceUpdateDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnnonceMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.label", target = "categoryName")
    AnnonceDTO toDTO(Annonce annonce);

    // Map implicitely categoryId from DTO back to category Object? Usually Spring
    // Data or Service handles ID fetching.
    // MapStruct can't fetch entity by ID automatically without a dependency mapper.
    // We'll ignore the category object and let the service handle it,
    // OR we map categoryId to category.id directly.
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    Annonce toEntity(AnnonceCreateDTO dto);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(AnnonceUpdateDTO dto, @MappingTarget Annonce annonce);

    List<AnnonceDTO> toDTOList(List<Annonce> annonces);
}

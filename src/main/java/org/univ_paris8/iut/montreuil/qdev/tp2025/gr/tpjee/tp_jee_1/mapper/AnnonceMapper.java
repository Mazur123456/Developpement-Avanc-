package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.Annonce;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnnonceMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorUsername")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.label", target = "categoryLabel")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    AnnonceDTO toDTO(Annonce entity);

    List<AnnonceDTO> toDTOList(List<Annonce> entities);
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnonceUpdateDTO {

    private String title;
    private String description;
    private String adress;

    @Email
    private String mail;

    private AnnonceStatus status;
    private Long categoryId;
}

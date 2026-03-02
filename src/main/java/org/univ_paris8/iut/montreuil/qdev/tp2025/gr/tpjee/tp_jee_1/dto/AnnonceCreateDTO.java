package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.AnnonceConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnonceCreateDTO {

    @NotBlank(message = AnnonceConstants.TITLE_REQUIRED)
    private String title;

    private String description;
    private String adress;

    @Email
    private String mail;

    private Long categoryId;
}

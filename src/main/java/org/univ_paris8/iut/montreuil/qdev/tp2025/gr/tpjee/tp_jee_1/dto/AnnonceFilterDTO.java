package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class AnnonceFilterDTO {
    private String q;
    private String status;
    private Long categoryId;
    private Long authorId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;
}

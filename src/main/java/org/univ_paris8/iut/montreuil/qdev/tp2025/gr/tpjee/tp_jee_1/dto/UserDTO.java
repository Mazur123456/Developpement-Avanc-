package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * DTO pour l'entité User. Ne contient jamais le mot de passe.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Timestamp createdAt;
    /**
     * Intentionnellement ignoré dans l'API — exclu via @Mapping(ignore=true) dans
     * UserMapper.
     */
    private String password;
}

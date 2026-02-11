package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitaire pour le hachage sécurisé des mots de passe.
 * Utilise SHA-256 pour ne pas stocker les mots de passe en clair.
 */
public class PasswordUtil {

    private PasswordUtil() {
        // Classe utilitaire
    }

    /**
     * Hache un mot de passe en SHA-256 (hex digest)
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 non disponible", e);
        }
    }

    /**
     * Vérifie si un mot de passe brut correspond au hash stocké
     */
    public static boolean checkPassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }
}

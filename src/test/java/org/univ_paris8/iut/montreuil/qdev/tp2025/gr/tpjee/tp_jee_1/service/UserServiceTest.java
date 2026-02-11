package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils.PasswordUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Niveau 2 – Tests unitaires du UserService
 * Focus : hachage mots de passe, logique d'authentification
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Test
    @DisplayName("Le hash SHA-256 est déterministe")
    void testHashIsDeterministic() {
        String hash1 = PasswordUtil.hashPassword("monMotDePasse");
        String hash2 = PasswordUtil.hashPassword("monMotDePasse");
        assertEquals(hash1, hash2, "Le même mot de passe doit produire le même hash");
    }

    @Test
    @DisplayName("Le hash SHA-256 produit 64 caractères hex")
    void testHashLength() {
        String hash = PasswordUtil.hashPassword("test");
        assertEquals(64, hash.length(), "SHA-256 produit un hash de 64 caractères hexadécimaux");
    }

    @Test
    @DisplayName("Le hash est différent du mot de passe en clair")
    void testHashDiffersFromPlaintext() {
        String password = "secret123";
        String hash = PasswordUtil.hashPassword(password);
        assertNotEquals(password, hash, "Le hash ne doit pas être identique au mot de passe");
    }

    @Test
    @DisplayName("checkPassword valide les mots de passe corrects")
    void testCheckPasswordCorrect() {
        String password = "monMotDePasse";
        String hash = PasswordUtil.hashPassword(password);
        assertTrue(PasswordUtil.checkPassword(password, hash));
    }

    @Test
    @DisplayName("checkPassword rejette les mots de passe incorrects")
    void testCheckPasswordIncorrect() {
        String hash = PasswordUtil.hashPassword("correct");
        assertFalse(PasswordUtil.checkPassword("incorrect", hash));
    }

    @Test
    @DisplayName("Des mots de passe différents produisent des hash différents")
    void testDifferentPasswordsDifferentHashes() {
        String hash1 = PasswordUtil.hashPassword("password1");
        String hash2 = PasswordUtil.hashPassword("password2");
        assertNotEquals(hash1, hash2);
    }
}

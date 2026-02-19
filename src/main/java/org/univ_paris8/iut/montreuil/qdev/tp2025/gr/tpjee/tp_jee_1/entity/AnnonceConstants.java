package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.entity;

/**
 * Constantes partagées pour la validation des annonces.
 * Utilisées à la fois dans l'entité Annonce et dans AnnonceDTO
 * pour éviter la duplication des tailles et messages.
 */
public final class AnnonceConstants {

    private AnnonceConstants() {
        // Classe utilitaire
    }

    // Tailles maximales des champs
    public static final int TITLE_MAX = 64;
    public static final int DESCRIPTION_MAX = 256;
    public static final int ADRESS_MAX = 64;
    public static final int MAIL_MAX = 64;

    // Messages de validation
    public static final String TITLE_REQUIRED = "Le titre est obligatoire";
    public static final String TITLE_TOO_LONG = "Le titre ne doit pas dépasser " + TITLE_MAX + " caractères";
    public static final String DESCRIPTION_TOO_LONG = "La description ne doit pas dépasser " + DESCRIPTION_MAX
            + " caractères";
    public static final String ADRESS_TOO_LONG = "L'adresse ne doit pas dépasser " + ADRESS_MAX + " caractères";
    public static final String MAIL_INVALID = "L'email doit être valide";
    public static final String MAIL_TOO_LONG = "L'email ne doit pas dépasser " + MAIL_MAX + " caractères";
}

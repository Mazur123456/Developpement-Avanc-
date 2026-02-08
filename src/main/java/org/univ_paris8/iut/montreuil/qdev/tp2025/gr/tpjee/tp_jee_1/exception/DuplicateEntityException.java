package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception;

/**
 * Exception levée lors d'une violation de contrainte d'unicité
 */
public class DuplicateEntityException extends BusinessException {

    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String field, String value) {
        super("La valeur '" + value + "' existe déjà pour le champ " + field);
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception;

/**
 * Exception métier de base pour l'application
 */
public class BusinessException extends Exception {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

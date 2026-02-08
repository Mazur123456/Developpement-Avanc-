package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception;

/**
 * Exception levée lors d'une transition d'état invalide
 */
public class InvalidStateException extends BusinessException {

    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String currentState, String expectedState) {
        super("État invalide: attendu " + expectedState + ", actuel: " + currentState);
    }
}

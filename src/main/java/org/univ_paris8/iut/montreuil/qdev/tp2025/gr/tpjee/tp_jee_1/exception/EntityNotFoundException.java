package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.exception;

/**
 * Exception levée lorsqu'une entité n'est pas trouvée
 */
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " avec l'ID " + id + " non trouvé(e)");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}

package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.utils;

import javax.persistence.EntityManager;

/**
 * Helper pour éviter la duplication du boilerplate EntityManager/Transaction
 * dans les services. Centralise la gestion du cycle de vie EntityManager
 * (ouverture, transaction, commit/rollback, fermeture).
 */
public final class TransactionTemplate {

    private TransactionTemplate() {
        // Classe utilitaire
    }

    /**
     * Interface fonctionnelle pour les opérations en transaction (create, update,
     * delete)
     */
    @FunctionalInterface
    public interface TransactionAction<T> {
        T execute(EntityManager em) throws Exception;
    }

    /**
     * Interface fonctionnelle pour les opérations void en transaction (delete)
     */
    @FunctionalInterface
    public interface TransactionVoidAction {
        void execute(EntityManager em) throws Exception;
    }

    /**
     * Interface fonctionnelle pour les lectures (findById, findAll, etc.)
     */
    @FunctionalInterface
    public interface ReadAction<T> {
        T execute(EntityManager em);
    }

    /**
     * Exécute une action dans une transaction JPA.
     * Gère automatiquement : begin, commit, rollback en cas d'erreur, et close.
     *
     * @param action L'action à exécuter dans la transaction
     * @return Le résultat de l'action
     */
    public static <T> T executeInTransaction(TransactionAction<T> action) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            T result = action.execute(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        } finally {
            em.close();
        }
    }

    /**
     * Exécute une action void dans une transaction JPA (ex: delete).
     */
    public static void executeInTransactionVoid(TransactionVoidAction action) {
        executeInTransaction(em -> {
            action.execute(em);
            return null;
        });
    }

    /**
     * Exécute une lecture (pas besoin de transaction).
     * Gère automatiquement : ouverture et fermeture de l'EntityManager.
     *
     * @param action L'action de lecture à exécuter
     * @return Le résultat de la lecture
     */
    public static <T> T executeReadOnly(ReadAction<T> action) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return action.execute(em);
        } finally {
            em.close();
        }
    }
}

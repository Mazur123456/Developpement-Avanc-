package org.univ_paris8.iut.montreuil.qdev.tp2025.gr.tpjee.tp_jee_1.dao;

import java.util.List;

/**
 * Classe abstraite générique DAO définissant les opérations CRUD de base
 * 
 * @param <T> Type de l'objet métier
 */
public abstract class DAO<T> {

    /**
     * Recherche un objet par son ID
     * 
     * @param id identifiant de l'objet
     * @return l'objet trouvé ou null
     */
    public abstract T find(int id);

    /**
     * Crée un nouvel objet dans la base de données
     * 
     * @param obj l'objet à créer
     * @return true si la création a réussi, false sinon
     */
    public abstract boolean create(T obj);

    /**
     * Met à jour un objet existant
     * 
     * @param obj l'objet à mettre à jour
     * @return true si la mise à jour a réussi, false sinon
     */
    public abstract boolean update(T obj);

    /**
     * Supprime un objet de la base de données
     * 
     * @param obj l'objet à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    public abstract boolean delete(T obj);

    /**
     * Récupère tous les objets de la table
     * 
     * @return liste de tous les objets
     */
    public abstract List<T> findAll();
}

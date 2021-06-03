package org.javastreet.utils;

import java.util.List;

public interface Table<T> {

	/**
	 * Création d'une nouvelle table
	 */
	void create();
	
	/**
	 * @param data à insérer dans la table
	 */
	void insert(T data);
	
	/**
	 * @param data à supprimer de la table
	 */
	void delete(T data);
	
	/**	
	 * Sélectionne toutes les données de la table
	 */
	void selectAll();
	
	/**
	 * Supprime toutes les données de la table
	 */
	void deleteAll();
	
	/**
	 * @param datas à supprimer de la table
	 */
	void deleteData(T... datas);
	
	/**
	 * @return une liste de données issues de la table
	 */
	List<T> getDatas();
}

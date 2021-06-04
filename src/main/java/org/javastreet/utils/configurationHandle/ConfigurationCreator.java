package org.javastreet.utils.configurationHandle;

import java.util.HashMap;

/**
 * Gestion des fichiers de configuration de l'application
 * @author groupe 5
 */
public class ConfigurationCreator {
	/* ensemble des fichiers de configuration de l'application*/
	private static HashMap<String, ConfigurationFile> configurationsFiles;
		
	private static ConfigurationCreator instance;
	
	private ConfigurationCreator() {
		configurationsFiles = new HashMap<>();
		loadConfigurationFiles();
	}
	
	/**
	 * Création d'une instance unique de {@link ConfigurationCreator}
	 * @return l'instance unique de cette classe
	 */
	public static ConfigurationCreator getInstance() {
		if(instance == null) {
			instance = new ConfigurationCreator();
		}
		return instance;
	}
	
	public HashMap<String, ConfigurationFile> getConfigurationFiles() {
		return configurationsFiles;
	}
	
	public ConfigurationFile getConfigurationFile(String key) {
		return configurationsFiles.get(key);
	}
	
	/**
	 * Chargement des fichiers de configuration
	 */
	private void loadConfigurationFiles() {
		configurationsFiles.put("configurationFileNavigator", new ConfigurationFileNavigator());
		configurationsFiles.put("configurationFileEngineSearch", new ConfigurationFileEngineSearch());
	}
}

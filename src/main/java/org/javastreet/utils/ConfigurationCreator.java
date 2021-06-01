package org.javastreet.utils;

import java.util.HashMap;

public class ConfigurationCreator {
	private static HashMap<String, ConfigurationFile> configurationsFiles;
		
	private static ConfigurationCreator instance;
	
	private ConfigurationCreator() {
		configurationsFiles = new HashMap<>();
		ddd();
	}
	
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
	
	public void ddd() {
		configurationsFiles.put("configurationFileNavigator", new ConfigurationFileNavigator());
		configurationsFiles.put("configurationFileEngineSearch", new ConfigurationFileEngineSearch());
	}
}

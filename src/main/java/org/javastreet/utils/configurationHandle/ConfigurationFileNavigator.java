package org.javastreet.utils.configurationHandle;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.javastreet.utils.Query;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConfigurationFileNavigator implements ConfigurationFile {

    private static String jsonNavigateurConfigPath = "/src/main/resources/config.json";
	
    private String engine;

    /**
     * Création d'une nouvelle instance et chargement du fichier de configuration
     */
    public ConfigurationFileNavigator() {
        this.load();
    }
    
    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getEngine(){
        return this.engine;
    }
	
	@Override
	public void load() {
		FileReader configFile = null;
        try {
            // lecture du fichier de config
            String basePath = System.getProperty("user.dir");
            JSONParser parser = new JSONParser();
            configFile = new FileReader(basePath + jsonNavigateurConfigPath);
            Object obj = parser.parse(configFile);

            JSONObject jsonObject = (JSONObject) obj;
            this.engine = (String)jsonObject.get("Engine");
            configFile.close();
        } catch (IOException | ParseException e){
            System.out.println("Attention le fichier de configuration n'existe pas ou non conforme, " +
                    "utilisation de la configuration par défaut : " + e.getMessage());

            this.engine = "google"; // moteur par défaut google
        }
	}

	@Override
	public void save() {
		String filePath = System.getProperty("user.dir") + jsonNavigateurConfigPath;
        JSONObject newConfig = new JSONObject();
        newConfig.put("Engine", this.engine);
        try {
            Files.write(Paths.get(filePath), newConfig.toJSONString().getBytes());
        } catch (IOException e){
            System.out.println("Erreur lors de l'écriture dans le fichier : " + e.getMessage());
        }
	}
	
    public String query(String url[]){
        return Query.request(this.engine, url);
    }

}

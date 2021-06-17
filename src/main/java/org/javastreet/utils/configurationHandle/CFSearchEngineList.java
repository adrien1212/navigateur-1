package org.javastreet.utils.configurationHandle;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CFSearchEngineList implements ConfigurationFile{

    private static String jsonMoteurRechercheConfigPath = "/src/main/resources/webEngine.json";
	
    private HashMap<String, String> availableEngine;

    /**
     * Création d'une nouvelle instance et chargement du fichier
     */
    public CFSearchEngineList() {
        this.availableEngine = new HashMap<>();
        this.load();
    }
    
    public HashMap<String, String> getAvailableEngine(){
        return this.availableEngine;
    }
    
    /**
     * Ajouter un nouveeau moteur de recherche
     * @param nom du moteur de recherche
     * @param url du moteur de recherche
     */
    public void addAvailableEngine(String nom, String url){
        this.availableEngine.put(nom, url);
    }

    /**
     * @param engine le moteur de recherche dont on veut savoir s'il est utilisable
     * @return true si le moteur de recherche peut être utilisé
     *         false
     */
    public boolean isAvailable(String engine) {
        return this.availableEngine.containsKey(engine);
    }
    
	@Override
	public void load() {
		try {
            // lecture du fichier des config de navigateur
            String basePath = System.getProperty("user.dir");
            JSONParser parser1 = new JSONParser();
            FileReader webEngineFile = new FileReader(basePath + jsonMoteurRechercheConfigPath);
            Object obj1 = parser1.parse(webEngineFile);
            webEngineFile.close();
            JSONObject jsonObject1 = (JSONObject) obj1;
            Set<String> keys = jsonObject1.keySet();
            for (String k : keys) {
                String v = (String) jsonObject1.get(k);
                this.availableEngine.put(k, v);
            }
        } catch (IOException | ParseException e){
            e.printStackTrace();
        }
	}

	@Override
	public void save() {
		String filePath = System.getProperty("user.dir") + jsonMoteurRechercheConfigPath;
        JSONObject newConfig = new JSONObject();
        for(Map.Entry<String, String> entry: this.availableEngine.entrySet()){
            newConfig.put(entry.getKey(), entry.getValue());
        }
        try {
            Files.write(Paths.get(filePath), newConfig.toJSONString().getBytes());
        } catch (IOException e) {
            System.out.println("Erreur lors de l'écriture dans le fichier : " + e.getMessage());
        }
	}
	
    public String getEngineURL() {
    	CFSearchEngineDefault cfn = (CFSearchEngineDefault) ConfigurationCreator.getInstance().getConfigurationFile("configurationFileNavigator");
        String url[] = this.availableEngine.get(cfn.getEngine()).split("/");
        return url[0] + "//" + url[2];
    }
}

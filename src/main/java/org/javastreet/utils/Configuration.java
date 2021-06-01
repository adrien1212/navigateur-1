package org.javastreet.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Configuration {

    /* Localisation des configuration du navigateur */
    private static String jsonNavigateurConfigPath = "/src/main/resources/config.json";

    /* Localisation des configuration des moteurs de recherche */
    private static String jsonMoteurRechercheConfigPath = "/src/main/resources/webEngine.json";

    private String engine;
    private HashMap<String, String> availableEngine;

    private static Configuration INSTANCE = null;

    private Configuration(){
        this.availableEngine = new HashMap<>();

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

    /**
     * @return l'objet configuration
     */
    public static Configuration getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Configuration();
            INSTANCE.loadWebConfig();
        }
        return INSTANCE;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getEngine(){
        return this.engine;
    }

    public String getEngineURL() {
        String url[] = this.availableEngine.get(this.engine).split("/");
        return url[0] + "//" + url[2];
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

    /**
     * Charge le fichier de configuration des moteurs de recherche du navigateur web
     */
    private void loadWebConfig(){
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

    /**
     * Sauvegarde la configuration du navigateur
     */
    public void saveToJson()  {
        String filePath = System.getProperty("user.dir") + jsonNavigateurConfigPath;
        JSONObject newConfig = new JSONObject();
        newConfig.put("Engine", getInstance().getEngine());
        try {
            Files.write(Paths.get(filePath), newConfig.toJSONString().getBytes());
        } catch (IOException e){
            System.out.println("Erreur lors de l'écriture dans le fichier : " + e.getMessage());
        }
    }

    /**
     * Sauvegarde la configuration des moteurs de recherche
     */
    public void saveWebConfToJson(){
        String filePath = System.getProperty("user.dir") + jsonMoteurRechercheConfigPath;
        JSONObject newConfig = new JSONObject();
        for(Map.Entry<String, String> entry: getInstance().availableEngine.entrySet()){
            newConfig.put(entry.getKey(), entry.getValue());
        }
        try {
            Files.write(Paths.get(filePath), newConfig.toJSONString().getBytes());
        } catch (IOException e) {
            System.out.println("Erreur lors de l'écriture dans le fichier : " + e.getMessage());
        }
    }

    public String query(String url[]){
        return Query.request(this.engine, url);
    }


}

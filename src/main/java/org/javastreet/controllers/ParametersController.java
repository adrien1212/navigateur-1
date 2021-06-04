package org.javastreet.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.javastreet.utils.configurationHandle.ConfigurationCreator;
import org.javastreet.utils.configurationHandle.ConfigurationFileEngineSearch;
import org.javastreet.utils.configurationHandle.ConfigurationFileNavigator;

import java.util.ArrayList;
import java.util.Map;

public class ParametersController {

    @FXML
    private ChoiceBox choiceBoxEngine;

    @FXML
    private TextField TF_nom;

    @FXML
    private TextField TF_url;

    @FXML
    private Button BTN_creer;

    @FXML
    private Button btnSave;

    @FXML
    private void initialize(){
        // get config from file
        ConfigurationCreator config = ConfigurationCreator.getInstance();

        ConfigurationFileNavigator configNavigator = 
        		(ConfigurationFileNavigator) config.getConfigurationFile("configurationFileNavigator");
        
        ConfigurationFileEngineSearch configEngineSearch =
        		(ConfigurationFileEngineSearch) config.getConfigurationFile("configurationFileEngineSearch");
        
        choiceBoxEngine.setValue(configNavigator.getEngine());
        for(Map.Entry<String, String> entry : configEngineSearch.getAvailableEngine().entrySet()){
            choiceBoxEngine.getItems().add(entry.getKey());
        }

        btnSave.setOnAction(event -> {
        	configNavigator.setEngine((String) choiceBoxEngine.getSelectionModel().getSelectedItem());
            configNavigator.save();
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        });

        BTN_creer.setOnAction(event -> {
            String nom = TF_nom.getText();
            String url = TF_url.getText();

            configEngineSearch.addAvailableEngine(nom, url);
            configEngineSearch.save();

            choiceBoxEngine.getItems().clear();
            choiceBoxEngine.setValue(configNavigator.getEngine());
            for(Map.Entry<String, String> entry : configEngineSearch.getAvailableEngine().entrySet()){
                choiceBoxEngine.getItems().add(entry.getKey());
            }
        });
    }

}

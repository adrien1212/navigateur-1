package org.javastreet.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.javastreet.utils.Configuration;

import java.util.ArrayList;
import java.util.Map;

public class ParametersController {

    private Configuration config;

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
        config = Configuration.getInstance();

        choiceBoxEngine.setValue(config.getEngine());
        for(Map.Entry<String, String> entry : config.getAvailableEngine().entrySet()){
            choiceBoxEngine.getItems().add(entry.getKey());
        }

        btnSave.setOnAction(event -> {
            config.setEngine((String) choiceBoxEngine.getSelectionModel().getSelectedItem());
            config.saveToJson();
            Stage stage = (Stage) btnSave.getScene().getWindow();
            stage.close();
        });

        BTN_creer.setOnAction(event -> {
            String nom = TF_nom.getText();
            String url = TF_url.getText();

            config.addAvailableEngine(nom, url);
            config.saveWebConfToJson();

            choiceBoxEngine.getItems().clear();
            choiceBoxEngine.setValue(config.getEngine());
            for(Map.Entry<String, String> entry : config.getAvailableEngine().entrySet()){
                choiceBoxEngine.getItems().add(entry.getKey());
            }
        });
    }

}

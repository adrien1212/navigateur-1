package org.javastreet.controllers;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.javastreet.models.HistoryEntry;
import org.javastreet.utils.DBHistory;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class HistoryController {

    static final long DAY = 24 * 60 * 60;

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private Button deleteButton;

    @FXML
    private ChoiceBox deleteChoice;

    @FXML
    private ListView<HistoryEntry> historyEntries;

    ObservableList<HistoryEntry> observableList = FXCollections.observableArrayList();
    DBHistory db;

    @FXML
    private void initialize() {
        db = new DBHistory();

        for (HistoryEntry he : db.getHistory()) {
            System.out.println(he);
            observableList.add(he);
        }

        historyEntries.setItems(observableList);

        // choice box
        deleteChoice.getItems().add("Selectionné");
        //deleteChoice.getItems().add("dernière 24H");
        deleteChoice.getItems().add("Tout");

        // Search
        searchBar.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                searchHistory();
            }
        });

        // Search Button click handler
        searchButton.setOnAction(event -> {
            searchHistory();
        });

        // Delete Button click handler
        deleteButton.setOnAction(event -> {
            String deleteOption = (String) deleteChoice.getValue();
            ObservableList<HistoryEntry> tempList = FXCollections.observableArrayList();
            if (deleteOption == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Suppresion");
                alert.setContentText("Attention merci de choisir un mode de suppresion");

                alert.showAndWait();
            } else if (deleteOption.equals("Selectionné")) {
                for (HistoryEntry he : historyEntries.getSelectionModel().getSelectedItems()) {
                    try {
                        db.delete(he);
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    } finally {
                        observableList.remove(he);
                    }
                }
            /*} else if (deleteOption.equals("dernière 24H")) {
                for (HistoryEntry he : db.getHistory()) {
                    Date now = new Date();
                    if (he.getDate().getTime() < now.getTime() - DAY) {
                        try {
                            db.delete(he);
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                        // observableList.remove(he);
                    } else {
                        tempList.add(he);
                    }
                }
            observableList = tempList;*/
            } else if (deleteOption.equals("Tout")) {
                db.deleteAll();
                observableList.clear();
            }

        });
    }

    private void searchHistory() {
        String searchText = searchBar.getText().toLowerCase();
        observableList.clear();
        for (HistoryEntry he : db.getHistory()) {
            if (he.getName().contains(searchText) || he.getLink().contains(searchText)) {
                observableList.add(he);
            }
        }
    }

}

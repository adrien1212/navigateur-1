package org.javastreet.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.javastreet.models.HistoryEntry;
import org.javastreet.utils.DBHistory;

public class HistoryController {

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<String> historyEntries;

    ObservableList<String> observableList = FXCollections.observableArrayList();
    DBHistory db;

    @FXML
    private void initialize()
    {
        db = new DBHistory();
        System.out.println(db.getHistory().size());
        for(HistoryEntry he : db.getHistory()) {
            System.out.println(he);
            observableList.add(he.getStringForHistory());
        }

        historyEntries.setItems(observableList);


        searchBar.setOnKeyPressed( event-> {
            if (event.getCode().equals(KeyCode.ENTER)){
                searchHistory();
            }
        });
        // Search Button click handler
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchHistory();
            }
        });
    }
    private void searchHistory() {
        String searchText = searchBar.getText().toLowerCase();
        observableList.clear();
        for (HistoryEntry he: db.getHistory()) {
            if (he.getName().contains(searchText) || he.getLink().contains(searchText)) {
                observableList.add(he.getStringForHistory());
            }
        }
    }
}

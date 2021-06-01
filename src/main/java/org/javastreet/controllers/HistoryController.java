package org.javastreet.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.javastreet.models.HistoryEntry;
import org.javastreet.utils.DBHistory;

public class HistoryController {

    @FXML
    ListView<String> historyEntries;
    ObservableList<String> observableList = FXCollections.observableArrayList();

    @FXML
    private void initialize()
    {
        DBHistory dh = new DBHistory();
        System.out.println(dh.getHistory().size());
        for(HistoryEntry he : dh.getHistory()) {
            System.out.println(he);
            observableList.add(he.getStringForHistory());
        }

        historyEntries.setItems(observableList);
    }
    
    
}

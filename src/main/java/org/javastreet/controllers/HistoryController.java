package org.javastreet.controllers;

import org.javastreet.models.HistoryEntry;
import org.javastreet.models.TabEntry;
import org.javastreet.utils.DBConnection;
import org.javastreet.utils.DB.TableHistory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

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

    private TabsController tabsController;

    ObservableList<HistoryEntry> observableList = FXCollections.observableArrayList();
    TableHistory db;

    @FXML
    private void initialize() {
        db = TableHistory.getInstance(DBConnection.getInstance());

        for (HistoryEntry he : db.getDatas()) {
            System.out.println(he);
            observableList.add(he);
        }

        historyEntries.setItems(observableList);

        // choice box
        deleteChoice.getItems().add("Selectionné");
        //deleteChoice.getItems().add("dernière 24H");
        deleteChoice.getItems().add("Tout");

        // When double clicking on history entries it loads the
        // page in a new tab
        historyEntries.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if (mouseEvent.getClickCount() == 2) {
                        EventTarget target = mouseEvent.getTarget();
                        if (target instanceof Text) {
                            // Parse the URL from the String element
                            String location = ((Text)target).getText();
                            location = location.split(" \\| ")[1].trim();
                            System.out.println(location);

                            // Load the URL in a new tab
                            tabsController.addNewTab(false);
                            TabEntry tab = tabsController.getCurrentTab();
                            tab.getWebView().getEngine().load(location);
                        }
                    }
                }
            }
        });

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
                alert.setHeaderText("Suppression");
                alert.setContentText("Attention merci de choisir un mode de Suppression");

                alert.showAndWait();
            } else if (deleteOption.equals("Selectionné")) {
                for (HistoryEntry he : historyEntries.getSelectionModel().getSelectedItems()) {
                    try {
                        db.delete(he);
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
        for (HistoryEntry he : db.getDatas()) {
            if (he.getName().contains(searchText) || he.getLink().contains(searchText)) {
                observableList.add(he);
            }
        }
    }

    private TabsController getTabsController() {
        return this.tabsController;
    }

    public void setTabsController(TabsController tabsController) {
        this.tabsController = tabsController;
    }

}

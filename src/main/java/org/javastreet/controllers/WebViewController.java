package org.javastreet.controllers;

import java.io.IOException;

import org.javastreet.utils.NavigationUtils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.javastreet.models.Bookmark;
import org.javastreet.models.BookmarkDir;
import org.javastreet.models.HistoryEntry;
import org.javastreet.utils.DBBookmarks;
import org.javastreet.utils.DBConnection;
import org.javastreet.utils.DBCookies;
import org.javastreet.utils.DBHistory;
import javafx.stage.Stage;


public class WebViewController
{
    @FXML
    private TextField addressBar;

    @FXML
    private Label stateLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button previousButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button refreshButton;
    
    @FXML
    private Button newTabButton;
    
    @FXML
    private Button newPrivateTabButton;

    @FXML
    private MenuButton menuButton;

    @FXML
    private MenuItem cookieMenu;

    @FXML
    private MenuItem historyMenu;

    @FXML
    private MenuItem bookmarkMenu;

    @FXML
    private VBox vBox;
    
    @FXML 
    private TabsController tabController;

    @FXML
    private MenuItem paramsMenu;
      
    @FXML
    private void initialize()
    {
		tabController.setControlsController(this);
		tabController.addNewTab(false);

        progressBar.prefWidthProperty().bind(vBox.widthProperty());
        
        addressBar.setOnKeyPressed( event-> {
                if (event.getCode().equals(KeyCode.ENTER)){
                	tabController.getCurrentTab().getTab().setText("Loading...");
                    NavigationUtils.search(addressBar.getText(), tabController.getCurrentTab().getWebView().getEngine());
                }
        });

        // Previous Button click handler
        previousButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.runLater(() -> {
                    // Interaction with the webview DOM to fetch the previous page
                	tabController.getCurrentTab().getTab().setText("Loading...");
                	tabController.getCurrentTab().getWebView().getEngine().executeScript("history.back()");
                });
            }
        });

        // Forward Button click handler
        forwardButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.runLater(() -> {
                    // Interaction with the webview DOM to fetch the forward page
                	tabController.getCurrentTab().getTab().setText("Loading...");
                	tabController.getCurrentTab().getWebView().getEngine().executeScript("history.forward()");
                });
            }
        });

        historyMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Parent root = null;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/history.fxml"));
                    root = loader.load();
                    HistoryController historyController = loader.getController();
                    historyController.setTabsController(tabController);

                    Stage stage = new Stage();
                    stage.setTitle("Historique");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        bookmarkMenu.setOnAction(new EventHandler<ActionEvent>() {
                                     @Override
                                     public void handle(ActionEvent actionEvent) {
                                         try {
                                             FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/bookmark.fxml"));
                                             Parent parent = fxmlLoader.load();
                                             BookmarkController bookmarkController = fxmlLoader.<BookmarkController>getController();
                                             bookmarkController.setTabsController(tabController);
                                             bookmarkController.setBookmark(new Bookmark(NavigationUtils.getTitle(tabController.getCurrentTab().getWebView().getEngine()), addressBar.getText()));

                                             Scene scene = new Scene(parent);
                                             Stage stage = new Stage();
                                             stage.setTitle("Favoris");
                                             stage.setScene(scene);
                                             stage.showAndWait();
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 });
        paramsMenu.setOnAction(event -> {
            Parent root = null;
            try{
                root = FXMLLoader.load(getClass().getResource("/fxml/parameters.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Paramètres");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        cookieMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Parent root = null;
                try {
                    tabController.saveCookies();
                    root = FXMLLoader.load(getClass().getResource("/fxml/cookie.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Cookie");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Refresh Button click handler
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Refresh the page
            	tabController.getCurrentTab().getWebView().getEngine().reload();
            }
        });

        newTabButton.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {
        		tabController.addNewTab(false);
            	tabController.getCurrentTab().getTab().setText("Loading...");
        		NavigationUtils.search(addressBar.getText(), tabController.getCurrentTab().getWebView().getEngine());
        	}
        });
        
        newPrivateTabButton.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {
        		tabController.addNewTab(true);
            	tabController.getCurrentTab().getTab().setText("Loading...");
        		NavigationUtils.search(addressBar.getText(), tabController.getCurrentTab().getWebView().getEngine());
        	}
        });
    }
    
    public TextField getAddressBar() {
    	return this.addressBar;
    }
    
    public ProgressBar getProgressBar() {
    	return this.progressBar;
    }
    
    public TabsController getTabsController() {
    	return this.tabController;
    }
}


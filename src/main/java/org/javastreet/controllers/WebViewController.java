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
import javafx.stage.Stage;

import org.javastreet.models.HistoryEntry;
import org.javastreet.utils.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.sql.Date;


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
    private MenuButton menuButton;

    @FXML
    private MenuItem cookieMenu;

    @FXML
    private MenuItem historyMenu;

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
		tabController.addNewTab();

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
                    root = FXMLLoader.load(getClass().getResource("/fxml/history.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Historique");
                    stage.setScene(new Scene(root));
                    stage.show();
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
        		tabController.addNewTab();
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


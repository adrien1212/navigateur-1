package org.javastreet.controllers;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;

public class WebViewController
{
    @FXML
    private WebView webView;

    @FXML
    private TextField addressBar;

    @FXML
    private Button goButton;

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
    private void initialize()
    {
        addressBar.setText("https://eclipse.org");
        WebEngine webEngine = webView.getEngine();
        Worker<Void> worker = webEngine.getLoadWorker();

        // Listening to the status of worker
        worker.stateProperty().addListener(new ChangeListener<State>() {
        	
            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                addressBar.setText(webEngine.getLocation());
            }
            
        });
        progressBar.progressProperty().bind(worker.progressProperty());

        goButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                String url = addressBar.getText();
                // Load the page.
                webEngine.load(url);
            }
        });
        
        // Previous Button click handler
        previousButton.setOnAction(new EventHandler<ActionEvent>() {
        	
        	@Override
        	public void handle(ActionEvent event) {
        		Platform.runLater(() -> {
        			// Interaction with the webview DOM to fetch the previous page
        			webEngine.executeScript("history.back()");
        		});
        	}
        	
        });
        
        // Forward Button click handler
        forwardButton.setOnAction(new EventHandler<ActionEvent>() {
        	
        	@Override
        	public void handle(ActionEvent event) {
        		Platform.runLater(() -> {
        			// Interaction with the webview DOM to fetch the forward page
        			webEngine.executeScript("history.forward()");
        		});
        	}
        	
        });
        
        // Refresh Button click handler
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {
        	
        	@Override
        	public void handle(ActionEvent event) {
        		// Refresh the page
        		webEngine.reload();
        	}
        	
        });
    }
}
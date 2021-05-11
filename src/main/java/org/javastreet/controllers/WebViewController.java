package org.javastreet.controllers;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

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
    private void initialize()
    {
        addressBar.setText("https://eclipse.org");
        WebEngine webEngine = webView.getEngine();
        Worker<Void> worker = webEngine.getLoadWorker();

        // Listening to the status of worker
        worker.stateProperty().addListener(new ChangeListener<State>() {

            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                stateLabel.setText("Loading state: " + newValue.toString());
                if (newValue == Worker.State.SUCCEEDED) {
                    // stage.setTitle(webEngine.getLocation());
                    stateLabel.setText("Finish!");
                }
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
    }
}
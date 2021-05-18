package org.javastreet.controllers;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;
import java.util.regex.*;

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
                addressBar.setText(webEngine.getLocation());
                if (newValue == Worker.State.SUCCEEDED) {
                    stateLabel.setText("Finish!");
                }
            }
        });
        progressBar.progressProperty().bind(worker.progressProperty());

        addressBar.setOnKeyPressed( event-> {
                if (event.getCode().equals(KeyCode.ENTER)){
                    search(webEngine);
                }
        });

        goButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(event.getEventType());
                search(webEngine);
            }
        });
    }

    private void search(WebEngine webEngine){
        String url = addressBar.getText();
        // Check if it's an url
        if(!isUrl(url)) {
            // check if it's an host
            if(isHost(url)){
                url = "https://" + url;
            } else {
                String keywords[] = url.split(" ");
                url = googleQuery(keywords);
            }
        }
        webEngine.load(url);
    }

    private Boolean isUrl(String url){
        Pattern urlPattern;
        Matcher urlMatcher;

        urlPattern = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
        urlMatcher = urlPattern.matcher(url);

        return urlMatcher.find();
    }

    private Boolean isHost(String host){
        Pattern urlPattern;
        Matcher urlMatcher;

        urlPattern = Pattern.compile("(\\w+\\.\\w+)+");
        urlMatcher = urlPattern.matcher(host);

        return urlMatcher.find();
    }

    private String googleQuery(String args[]){
        String keywords;
        String url;

        keywords = String.join("+", args);
        url = "https://www.google.com/search?q=" + keywords;

        // check if well formed
        if(isUrl(url)){
            return url;
        }
        return "";
    }
}
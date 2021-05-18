package org.javastreet.controllers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import org.javastreet.models.HistoryEntry;
import org.javastreet.utils.DBConnection;
import org.javastreet.utils.DBHistory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.sql.Date;

public class WebViewController
{
    @FXML
    private WebView webView;

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
    private MenuButton menuButton;

    @FXML
    private MenuItem historyMenu;

    private DBHistory myHistory;

    @FXML
    private void initialize()
    {
        addressBar.setText("https://www.google.com");
        myHistory = new DBHistory();
        WebEngine webEngine = webView.getEngine();
        Worker<Void> worker = webEngine.getLoadWorker();

        // Listening to the status of worker
        worker.stateProperty().addListener(new ChangeListener<State>() {

            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                addressBar.setText(webEngine.getLocation());
                if (newValue == Worker.State.SUCCEEDED) {
                    myHistory.insert(new HistoryEntry(getTitle(webEngine), webEngine.getLocation(), new java.util.Date()));
                }
            }

        });
        progressBar.progressProperty().bind(worker.progressProperty());
        
        addressBar.setOnKeyPressed( event-> {
                if (event.getCode().equals(KeyCode.ENTER)){
                    search(webEngine);
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

        // Refresh Button click handler
        refreshButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                // Refresh the page
                webEngine.reload();
            }

        });

        search(webEngine);
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

    private String getTitle(WebEngine webEngine) {
        Document doc = webEngine.getDocument();
        NodeList heads = doc.getElementsByTagName("head");
        String titleText = webEngine.getLocation() ; // use location if page does not define a title
        if (heads.getLength() > 0) {
            Element head = (Element)heads.item(0);
            NodeList titles = head.getElementsByTagName("title");
            if (titles.getLength() > 0) {
                Node title = titles.item(0);
                titleText = title.getTextContent();
            }
        }
        return titleText ;
    }
}


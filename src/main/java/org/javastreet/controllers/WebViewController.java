package org.javastreet.controllers;

import java.io.IOException;
import java.net.*;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
import org.javastreet.utils.DBCookies;
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
    private MenuItem cookieMenu;

    @FXML
    private MenuItem historyMenu;

    private DBHistory myHistory;

    @FXML
    private VBox vBox;

    private CookieManager cookieManager;

    private DBCookies myCookies;

    @FXML
    private void initialize()
    {
        addressBar.setText("https://www.google.com");
        myHistory = new DBHistory();
        myCookies = new DBCookies();
        WebEngine webEngine = webView.getEngine();

        // Cookie Manager
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        // Load cookies into webview when starting the app
        myCookies.getCookiesList().forEach(cookie -> {
            cookieManager.getCookieStore().add(URI.create(cookie.getDomain()), cookie);
        });

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

        // Bind progress bar to loading status of the worker
        progressBar.progressProperty().bind(worker.progressProperty());
        progressBar.prefWidthProperty().bind(vBox.widthProperty());

        // Hide the progress bar once the page has loaded completely
        worker.stateProperty().addListener(new ChangeListener<State>() {
            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                if (newValue == Worker.State.SUCCEEDED) {
                    progressBar.setOpacity(0);
                } else {
                    progressBar.setOpacity(1);
                }
            }
        });
        
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

        cookieMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Parent root = null;
                try {
                    saveCookies();
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

        settingsMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cookieManager.getCookieStore().removeAll();
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

    public void saveCookies() {
        // Delete all cookies from the DB so that we don't store duplicates
        // and only store cookies from the webview that are not stale
        // (the webview automatically delete)
        myCookies.deleteAll();
        cookieManager.getCookieStore().getCookies().forEach(cookie -> {
            myCookies.insert(cookie);
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


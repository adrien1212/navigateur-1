package org.javastreet.controllers;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private Button goButton;

    @FXML
    private Label stateLabel;

    @FXML
    private ProgressBar progressBar;

    private DBHistory myHistory;

    @FXML
    private void initialize()
    {
        myHistory = new DBHistory();

        addressBar.setText("https://eclipse.org");
        myHistory.insert(new HistoryEntry("https://eclipse.org", "https://eclipse.org", new java.util.Date()));
        WebEngine webEngine = webView.getEngine();
        Worker<Void> worker = webEngine.getLoadWorker();

        // Listening to the status of worker
        worker.stateProperty().addListener(new ChangeListener<State>() {

            @Override
            public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                stateLabel.setText("Loading state: " + newValue.toString());
                addressBar.setText(webEngine.getLocation());
                if (newValue == Worker.State.SUCCEEDED) {
                    myHistory.insert(new HistoryEntry(getTitle(webEngine), webEngine.getLocation(), new java.util.Date()));
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


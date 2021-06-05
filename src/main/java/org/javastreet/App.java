package org.javastreet;

import org.javastreet.controllers.WebViewController;
import org.javastreet.utils.DBConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(final Stage stage) throws Exception {
      
        DBConnection connection = DBConnection.getInstance();
        connection.connect();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();
        WebViewController webViewController = loader.getController();

        // Parent root = FXMLLoader.load(getClass().getResource("/fxml/main.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setOnHidden(e -> webViewController.getTabsController().saveCookies());
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
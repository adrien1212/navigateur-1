package org.javastreet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.javastreet.utils.DBConnection;
import org.javastreet.utils.DBHistory;

import java.sql.DriverManager;

public class App extends Application {

    public static DBConnection connection;

    @Override
    public void start(final Stage stage) throws Exception {
        connection = new DBConnection("src/main/resources/Database.db");
        connection.connect();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/history.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
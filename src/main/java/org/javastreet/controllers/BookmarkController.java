package org.javastreet.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.javastreet.models.Bookmark;
import org.javastreet.models.BookmarkDir;
import org.javastreet.utils.DBBookmarks;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class BookmarkController {
    @FXML
    private TreeView treeView;

    @FXML
    private TextField name;

    @FXML
    private TextField link;

    @FXML
    private Button addButton;

    @FXML
    private Button addDirButton;

    @FXML
    private ChoiceBox choices;;


    private DBBookmarks bookmarks;


    @FXML
    private void initialize() {
        bookmarks = new DBBookmarks();
        this.treeView.setRoot(createTreeView());
        this.treeView.setShowRoot(false);
        loadChoices();


        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                BookmarkDir d = (BookmarkDir)choices.getValue();
                Bookmark temp = new Bookmark(name.getText(), link.getText(), d.getId());
                bookmarks.addBookmark(temp);
                treeView.setRoot(createTreeView());
            }
        });

        addDirButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Ajouter un dossier");

                ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

                GridPane gridPane = new GridPane();
                gridPane.setHgap(10);
                gridPane.setVgap(10);

                TextField dirName = new TextField();
                dirName.setPromptText("dirName");

                gridPane.add(dirName, 0, 0);

                dialog.getDialogPane().setContent(gridPane);

                // Request focus on the username field by default.
                Platform.runLater(() -> dirName.requestFocus());

                // Convert the result to a username-password-pair when the login button is clicked.
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == loginButtonType) {
                        return new String(dirName.getText());
                    }
                    return null;
                });

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(pair -> {
                    bookmarks.addBoormarkDir(new BookmarkDir(pair));
                    loadChoices();
                });
            }
        });

    }

    private void loadChoices() {
        choices.getItems().clear();
        for (BookmarkDir d: bookmarks.getBookmarkDirs()) {
            choices.getItems().add(d);
        }
    }

    private TreeItem<String> createTreeView() {
        TreeItem<String> dummyRoot = new TreeItem<>();
        ArrayList<BookmarkDir> dirs = bookmarks.getBookmarkDirs();

        for (BookmarkDir d: dirs) {
            TreeItem<String> dir = new TreeItem<>(d.getName());
            for (Bookmark b : d.getBookmarks()) {
                dir.getChildren().add(new TreeItem<String>(b.toBookmarkString()));
            }
            dummyRoot.getChildren().add(dir);
        }
        return dummyRoot;
    }

    public void setBookmark(Bookmark bookmark) {
        name.setText(bookmark.getName());
        link.setText(bookmark.getLink());
    }
}

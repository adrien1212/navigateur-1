package org.javastreet.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.javastreet.models.HistoryEntry;
import org.javastreet.utils.DBCookies;
import org.javastreet.utils.DBHistory;

import java.net.HttpCookie;

public class CookieViewController {

    @FXML
    ListView<String> cookieEntries;
    ObservableList<String> observableList = FXCollections.observableArrayList();

    @FXML
    private void initialize()
    {
        DBCookies dbCookies = new DBCookies();
        System.out.println(dbCookies.getCookiesList().size());
        for(HttpCookie cookie : dbCookies.getCookiesList()) {
            System.out.println(cookie.getComment() + " " + cookie.getCommentURL() + " " + cookie.getDiscard() + " " + cookie.getDomain() + " " + cookie.isHttpOnly() + " " + cookie.getMaxAge() + " " + cookie.getPath() + " " + cookie.getPortlist() + " " + cookie.getSecure() + " " + cookie.getVersion());
            observableList.add(cookie.getComment() + " " + cookie.getCommentURL() + " " + cookie.getDiscard() + " " + cookie.getDomain() + " " + cookie.isHttpOnly() + " " + cookie.getMaxAge() + " " + cookie.getPath() + " " + cookie.getPortlist() + " " + cookie.getSecure() + " " + cookie.getVersion());
        }

        cookieEntries.setItems(observableList);
    }
}

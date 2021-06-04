package org.javastreet.controllers;

import java.net.HttpCookie;

import org.javastreet.utils.DBConnection;
import org.javastreet.utils.DB.TableCookies;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class CookieViewController {

    @FXML
    ListView<String> cookieEntries;
    ObservableList<String> observableList = FXCollections.observableArrayList();

    @FXML
    private void initialize()
    {
    	TableCookies dbCookies = TableCookies.getInstance(DBConnection.getInstance());
        System.out.println(dbCookies.getDatas().size());
        for(HttpCookie cookie : dbCookies.getDatas()) {
            System.out.println(cookie.getComment() + " " + cookie.getCommentURL() + " " + cookie.getDiscard() + " " + cookie.getDomain() + " " + cookie.isHttpOnly() + " " + cookie.getMaxAge() + " " + cookie.getPath() + " " + cookie.getPortlist() + " " + cookie.getSecure() + " " + cookie.getVersion());
            observableList.add(cookie.getComment() + " " + cookie.getCommentURL() + " " + cookie.getDiscard() + " " + cookie.getDomain() + " " + cookie.isHttpOnly() + " " + cookie.getMaxAge() + " " + cookie.getPath() + " " + cookie.getPortlist() + " " + cookie.getSecure() + " " + cookie.getVersion());
        }

        cookieEntries.setItems(observableList);
    }
}

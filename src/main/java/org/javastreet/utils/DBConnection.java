package org.javastreet.utils;

import java.sql.*;

public class DBConnection {

    private String DBPath = "";
    private Connection connection = null;
    private Statement statement = null;

    public DBConnection(String dBPath) {
        DBPath = dBPath;
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DBPath);
            statement = connection.createStatement();
            System.out.println("Connexion a " + DBPath + " avec succès");
        } catch (ClassNotFoundException notFoundException) {
            notFoundException.printStackTrace();
            System.out.println("Erreur de connecxion");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.out.println("Erreur de connecxion");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void createTable(String requet) {
        try {
            statement.execute(requet);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur dans la création de la BDD : \n" + requet);
        }
    }

    public void close() {
        try {
            connection.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

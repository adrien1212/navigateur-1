package org.javastreet.utils;

import java.sql.*;

public class DBConnection {

    private static final String DB_PATH = "src/main/resources/Database.db";
    
    private Connection connection = null;
    private Statement statement = null;

    private static DBConnection instance;
    
    private DBConnection() {
    }
    
    public static DBConnection getInstance() {
    	if (instance == null) {
    		instance = new DBConnection();
    	}
    	return instance;
    }

    public void connect() {
    	if(connection == null) {
    		try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
                statement = connection.createStatement();
                System.out.println("Connexion a " + DB_PATH + " avec succès");
            } catch (ClassNotFoundException notFoundException) {
                notFoundException.printStackTrace();
                System.out.println("Erreur de connecxion");
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                System.out.println("Erreur de connecxion");
            }
    	}
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
    
    public Connection getConnection() {
        return connection;
    }
}

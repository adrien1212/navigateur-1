package org.javastreet.poc.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connexion {
	private String DBPath = "";
    private Connection connection = null;
    private Statement statement = null;
 
    public Connexion(String dBPath) {
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
    
    public void insert(String name, double capacity) {
        String sql = "INSERT INTO warehouses(name,capacity) VALUES(?,?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public ResultSet selectAll(){
        String sql = "SELECT id, name, capacity FROM warehouses";
        
        try {
             Statement stmt  = connection.createStatement();
             return stmt.executeQuery(sql);
            
            // loop through the result set
//            while (rs.next()) {
//                System.out.println(rs.getInt("id") +  "\t" + 
//                                   rs.getString("name") + "\t" +
//                                   rs.getDouble("capacity"));
//            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
		return null;
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

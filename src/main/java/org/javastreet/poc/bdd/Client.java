package org.javastreet.poc.bdd;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Client {
	 
    public static void main(String[] args) throws SQLException {
        Connexion connexion = new Connexion("src/main/resources/Database.db");
        connexion.connect();
        
        
        String sql = "CREATE TABLE IF NOT EXISTS warehouses (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	capacity real\n"
                + ");";
        
        connexion.createTable(sql);
             
        connexion.insert("salut", 15);
        
        ResultSet rs = connexion.selectAll();
        
        while(rs.next()) {
        	System.out.println(rs.getInt("id") +  "\t" + 
                  rs.getString("name") + "\t" +
                  rs.getDouble("capacity"));
        }
        
        connexion.close();
    }
 
}

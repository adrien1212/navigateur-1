package org.javastreet.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.javastreet.App;
import org.javastreet.models.HistoryEntry;

public class TableHistory implements Table<HistoryEntry> {

	private List<HistoryEntry> datas;
	private DBConnection DBconnection;
	
	/**
	 * If the table isn't created, It creates it
	 * And select all datas in the table
	 * @param connection the database connection object
	 */
	public TableHistory(DBConnection DBconnection) {
		this.DBconnection = DBconnection;
		
		this.create();
		this.selectAll();
	}
		
	@Override
	public void create() {
		String sql = "CREATE TABLE IF NOT EXISTS History (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + " link text NOT NULL,\n"
                + "	date DATETIME\n"
                + ");";
		DBconnection.createTable(sql);
	}

	@Override
	public void insert(HistoryEntry data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(HistoryEntry data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectAll() {
		String sql = "SELECT * FROM History";

        try {
            Statement stmt  = DBconnection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                HistoryEntry he = new HistoryEntry(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("link"),
                        rs.getDate("date")
                );
                history.add(he);
                
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteData(HistoryEntry... datas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<HistoryEntry> getDatas() {
		// TODO Auto-generated method stub
		return null;
	}

}

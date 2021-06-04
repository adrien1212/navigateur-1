package org.javastreet.utils.DB;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.javastreet.models.HistoryEntry;
import org.javastreet.utils.DBConnection;

public class TableHistory implements Table<HistoryEntry> {

	private List<HistoryEntry> historyList;
	private DBConnection DBconnection;

	private static TableHistory instance;
	
	/**
	 * If the table isn't created, It creates it
	 * And select all datas in the table
	 * @param connection the database connection object
	 */
	private TableHistory(DBConnection DBconnection) {
		this.DBconnection = DBconnection;
		this.historyList = new ArrayList<HistoryEntry>();
		
		this.create();
		this.selectAll();
	}
	
	public static TableHistory getInstance(DBConnection DBconnection) {
		if(instance == null) {
			instance = new TableHistory(DBconnection);
		}
		return instance;
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
		int id = -1;
        String sql = "INSERT INTO History(name,link, date) VALUES(?,?,?)";

        try {
            PreparedStatement pstmt = DBconnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, data.getName());
            pstmt.setString(2, data.getLink());
            pstmt.setDate(3, new Date(data.getDate().getTime()));
            pstmt.executeUpdate();
            ResultSet rsKey = pstmt.getGeneratedKeys();
            if (rsKey.next()){
                id = rsKey.getInt(1);
            } else {
                throw new SQLException("Erreur dans la récupération de l'ID");
            }
        } catch (SQLException e) {
            System.out.println(data);
            System.out.println(e.getMessage());
        } finally {
        	data.setId(id);
            historyList.add(data);
        }

	}

	@Override
	public void delete(HistoryEntry data) {
		String sql = "DELETE FROM History WHERE id=?";

        try {
            PreparedStatement pstmt = DBconnection.getConnection().prepareStatement(sql);
            pstmt.setInt(1, data.getId());
            int retour = pstmt.executeUpdate();
            if(retour != 1){
                throw new SQLException("Erreur dans la suppresion");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            //history.remove(he);
            historyList.clear();
            selectAll();
        }
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
				historyList.add(he);

			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void deleteAll() {
		String sql = "DELETE FROM History";

        try {
            PreparedStatement pstmt = DBconnection.getConnection().prepareStatement(sql);
            int retour = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            historyList.clear();
        }
	}

	@Override
	public void deleteData(HistoryEntry... datas) {
        for(HistoryEntry he: datas){
            delete(he);
        }
	}

	@Override
	public List<HistoryEntry> getDatas() {
		return this.historyList;
	}

}

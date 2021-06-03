package org.javastreet.utils.DB;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.javastreet.models.BookmarkDir;
import org.javastreet.utils.DBConnection;

public class TableBookmarksDir implements Table<BookmarkDir>{

    private ArrayList<BookmarkDir> bookmarkDirsList;
    private DBConnection DBconnection;
	
    private static TableBookmarksDir instance;
    
	private TableBookmarksDir(DBConnection DBconnection) {
		this.DBconnection = DBconnection;
		this.bookmarkDirsList = new ArrayList<BookmarkDir>();

		this.create();
		this.selectAll();
	}
	
	public static TableBookmarksDir getInstance(DBConnection DBconnection) {
		if(instance == null) {
			instance = new TableBookmarksDir(DBconnection);
		}
		return instance;
	}
	
	@Override
	public void create() {
        String sql = "CREATE TABLE IF NOT EXISTS BookmarkDir (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL\n"
                + ");";

        DBconnection.createTable(sql);
	}

	@Override
	public void insert(BookmarkDir data) {
		String rq = "INSERT INTO BookmarkDir(name) VALUES (?)";
        int id = -1;
        try {
            PreparedStatement pstmt = DBconnection.getConnection().prepareStatement(rq, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, data.getName());
            pstmt.executeUpdate();
            ResultSet rsKey = pstmt.getGeneratedKeys();
            if (rsKey.next()){
                id = rsKey.getInt(1);
            } else {
                throw new SQLException("Erreur dans la récupération de l'ID");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            data.setId(id);
            bookmarkDirsList.add(data);
        }
	}

	@Override
	public void delete(BookmarkDir data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selectAll() {
		String rq = "SELECT * FROM BookmarkDir";
        try {
            Statement stmt  = DBconnection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(rq);

            // loop through the result set
            while (rs.next()) {
                BookmarkDir bookmarkDir = new BookmarkDir(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                bookmarkDirsList.add(bookmarkDir);
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
	public void deleteData(BookmarkDir... datas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<BookmarkDir> getDatas() {
		return bookmarkDirsList;
	}

}

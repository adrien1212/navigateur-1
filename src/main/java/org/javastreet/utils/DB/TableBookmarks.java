package org.javastreet.utils.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.javastreet.App;
import org.javastreet.models.Bookmark;
import org.javastreet.models.BookmarkDir;
import org.javastreet.utils.DBConnection;

public class TableBookmarks implements Table<Bookmark>{

	private DBConnection DBconnection;

	private static TableBookmarks instance;
	
	private TableBookmarks(DBConnection DBconnection) {
		this.DBconnection = DBconnection;
		create();
		selectAll();
	}
	
	public static TableBookmarks getInstance(DBConnection DBconnection) {
		if(instance == null) {
			instance = new TableBookmarks(DBconnection);
		}
		return instance;
	}
	
	@Override
	public void create() {
		String sqlBookmark = "CREATE TABLE IF NOT EXISTS Bookmark (\n"
				+ "	id integer PRIMARY KEY,\n"
				+ "	name text NOT NULL,\n"
				+ " link text NOT NULL,\n"
				+ " dirId integer NOT NULL, \n "
				+ " FOREIGN KEY (dirId)\n" +
				"       REFERENCES BookMarkDir(id) "
				+ ");";
		DBconnection.createTable(sqlBookmark);
	}

	@Override
	public void insert(Bookmark data) {
        String rq = "INSERT INTO Bookmark(name, link, dirId) VALUES (?,?,?)";
        int id = -1;

        try {
            PreparedStatement pstmt = DBconnection.getConnection().prepareStatement(rq, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, data.getName());
            pstmt.setString(2, data.getLink());
            pstmt.setInt(3, data.getDirId());
            pstmt.executeUpdate();
            ResultSet rsKey = pstmt.getGeneratedKeys();
            if (rsKey.next()){
                id = rsKey.getInt(1);
            } else {
                throw new SQLException("Erreur dans la récupération de l'ID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            data.setId(id);
            insertBookmark(data);
        }
	}
	
    private void insertBookmark(Bookmark b) {
        Optional<BookmarkDir> result = TableBookmarksDir.getInstance(DBconnection).getDatas().stream().filter(obj -> obj.getId()== b.getDirId()).findFirst();
        result.get().addBookmark(b);
    }

	@Override
	public void delete(Bookmark data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectAll() {
		String rq = "SELECT * FROM Bookmark";
        try {
            Statement stmt  = DBconnection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(rq);

            // loop through the result set
            while (rs.next()) {
                Bookmark b = new Bookmark(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("link"),
                        rs.getInt("dirId")
                );
                insertBookmark(b);
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
	public void deleteData(Bookmark... datas) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Bookmark> getDatas() {
		// TODO Auto-generated method stub
		return null;
	}

}

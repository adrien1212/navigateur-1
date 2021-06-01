package org.javastreet.utils;

import org.javastreet.App;
import org.javastreet.models.Bookmark;
import org.javastreet.models.BookmarkDir;
import org.javastreet.models.HistoryEntry;
import sun.tools.jconsole.JConsole;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class DBBookmarks {

    private ArrayList<BookmarkDir> bookmarkDirs;
    private Connection connection;

    public DBBookmarks() {
        connection= App.connection.getConnection();
        bookmarkDirs = new ArrayList<>();
        String sql = "CREATE TABLE IF NOT EXISTS BookmarkDir (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL\n"
                + ");";

        App.connection.createTable(sql);

        String sqlBookmark = "CREATE TABLE IF NOT EXISTS Bookmark (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + " link text NOT NULL,\n"
                + " dirId integer NOT NULL, \n "
                + " FOREIGN KEY (dirId)\n" +
                "       REFERENCES BookMarkDir(id) "
                + ");";
        App.connection.createTable(sqlBookmark);
        this.selectAll();
    }

    public ArrayList<BookmarkDir> getBookmarkDirs() {
        return bookmarkDirs;
    }

    public void setBookmarkDirs(ArrayList<BookmarkDir> bookmarkDirs) {
        this.bookmarkDirs = bookmarkDirs;
    }

    public void addBookmark(Bookmark b) {
        String rq = "INSERT INTO Bookmark(name, link, dirId) VALUES (?,?,?)";
        int id = -1;

        try {
            PreparedStatement pstmt = connection.prepareStatement(rq, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, b.getName());
            pstmt.setString(2, b.getLink());
            pstmt.setInt(3, b.getDirId());
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
            b.setId(id);
            insertBookmark(b);
        }
    }

    private void insertBookmark(Bookmark b) {
        Optional<BookmarkDir> result = bookmarkDirs.stream().filter(obj -> obj.getId()== b.getDirId()).findFirst();
        result.get().addBookmark(b);
    }

    public void addBoormarkDir(BookmarkDir d) {
        String rq = "INSERT INTO BookmarkDir(name) VALUES (?)";
        int id = -1;
        try {
            PreparedStatement pstmt = connection.prepareStatement(rq, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, d.getName());
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
            d.setId(id);
            bookmarkDirs.add(d);
        }
    }

    private void selectAll() {
        loadBookmarksDir();
        loadBookmarks();
    }

    private void loadBookmarksDir() {
        String rq = "SELECT * FROM BookmarkDir";
        try {
            Statement stmt  = connection.createStatement();
            ResultSet rs = stmt.executeQuery(rq);

            // loop through the result set
            while (rs.next()) {
                BookmarkDir d = new BookmarkDir(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                bookmarkDirs.add(d);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void loadBookmarks() {
        String rq = "SELECT * FROM Bookmark";
        try {
            Statement stmt  = connection.createStatement();
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
}

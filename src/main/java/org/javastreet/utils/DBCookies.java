package org.javastreet.utils;

import org.javastreet.App;

import java.net.HttpCookie;
import java.sql.*;
import java.util.ArrayList;

public class DBCookies {

    private ArrayList<HttpCookie> cookiesList;
    private Connection connection;

    public DBCookies() {
        connection= App.connection.getConnection();
        cookiesList = new ArrayList<>();
        String sql = "CREATE TABLE IF NOT EXISTS Cookie (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	comment TEXT,\n"
                + " commentURL TEXT,\n"
                + "	discard INTEGER,\n"
                + "	domain TEXT,\n"
                + "	maxAge INTEGER,\n"
                + "	name TEXT,\n"
                + "	path TEXT,\n"
                + "	portList TEXT,\n"
                + "	secure INTEGER,\n"
                + "	value TEXT,\n"
                + "	version INTEGER,\n"
                + "	httpOnly INTEGER\n"
                + ");";
        App.connection.createTable(sql);
        this.selectAll();
    }

    public void selectAll(){
        String sql = "SELECT * FROM Cookie";

        try {
            Statement stmt  = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                HttpCookie cookie = new HttpCookie(rs.getString("name"), rs.getString("value"));
                cookie.setComment(rs.getString("comment"));
                cookie.setCommentURL(rs.getString("commentURL"));
                cookie.setDiscard(rs.getBoolean("discard"));
                cookie.setDomain(rs.getString("domain"));
                cookie.setHttpOnly(rs.getBoolean("httpOnly"));
                cookie.setMaxAge(rs.getLong("maxAge"));
                cookie.setPath(rs.getString("path"));
                cookie.setPortlist(rs.getString("portList"));
                cookie.setSecure(rs.getBoolean("secure"));
                cookie.setVersion(rs.getInt("version"));
                System.out.println(cookie.getComment() + " " + cookie.getCommentURL() + " " + cookie.getDiscard() + " " + cookie.getDomain() + " " + cookie.isHttpOnly() + " " + cookie.getMaxAge() + " " + cookie.getPath() + " " + cookie.getPortlist() + " " + cookie.getSecure() + " " + cookie.getVersion());
                cookiesList.add(cookie);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(HttpCookie cookie) {
        String sql = "INSERT INTO Cookie(comment, commentURL, discard, domain, maxAge, name, path, portList, secure, value, version, httpOnly) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, cookie.getComment());
            pstmt.setString(2, cookie.getCommentURL());
            pstmt.setBoolean(3, cookie.getDiscard());
            pstmt.setString(4, cookie.getDomain());
            pstmt.setLong(5, cookie.getMaxAge());
            pstmt.setString(6, cookie.getName());
            pstmt.setString(7, cookie.getPath());
            pstmt.setString(8, cookie.getPortlist());
            pstmt.setBoolean(9, cookie.getSecure());
            pstmt.setString(10, cookie.getValue());
            pstmt.setInt(11, cookie.getVersion());
            pstmt.setBoolean(12, cookie.isHttpOnly());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            cookiesList.add(cookie);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM Cookie";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<HttpCookie> getCookiesList() {
        return cookiesList;
    }
}

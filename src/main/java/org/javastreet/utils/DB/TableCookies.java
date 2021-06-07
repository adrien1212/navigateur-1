package org.javastreet.utils.DB;

import java.net.HttpCookie;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class TableCookies implements Table<HttpCookie> {

	private ArrayList<HttpCookie> cookiesList;
	private DBConnection DBconnection;

	private static TableCookies instance;
	
	/**
	 * If the table isn't created, It creates it
	 * And select all datas in the table
	 * @param connection the database connection object
	 */
	private TableCookies(DBConnection DBconnection) {
		this.DBconnection = DBconnection;
		this.cookiesList = new ArrayList<HttpCookie>();

		this.create();
		this.selectAll();
	}
	
	public static TableCookies getInstance(DBConnection DBconnection) {
		if(instance == null) {
			instance = new TableCookies(DBconnection);
		}
		return instance;
	}

	@Override
	public void create() {
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
		DBconnection.createTable(sql);

	}

	@Override
	public void insert(HttpCookie data) {
		String sql = 
				"INSERT INTO Cookie(comment, commentURL, discard, domain, maxAge, name, path, portList, secure, value, version, httpOnly) "
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			PreparedStatement pstmt = DBconnection.getConnection().prepareStatement(sql);
			pstmt.setString(1, data.getComment());
			pstmt.setString(2, data.getCommentURL());
			pstmt.setBoolean(3, data.getDiscard());
			pstmt.setString(4, data.getDomain());
			pstmt.setLong(5, data.getMaxAge());
			pstmt.setString(6, data.getName());
			pstmt.setString(7, data.getPath());
			pstmt.setString(8, data.getPortlist());
			pstmt.setBoolean(9, data.getSecure());
			pstmt.setString(10, data.getValue());
			pstmt.setInt(11, data.getVersion());
			pstmt.setBoolean(12, data.isHttpOnly());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			cookiesList.add(data);
		}

	}

	@Override
	public void delete(HttpCookie data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectAll() {
		String sql = "SELECT * FROM Cookie";

		try {
			Statement stmt  = DBconnection.getConnection().createStatement();
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

	@Override
	public void deleteAll() {
		String sql = "DELETE FROM Cookie";
		try {
			PreparedStatement pstmt = DBconnection.getConnection().prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void deleteData(HttpCookie... datas) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<HttpCookie> getDatas() {
		return this.cookiesList;
	}

}

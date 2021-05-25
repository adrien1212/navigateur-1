package org.javastreet.utils;

import org.javastreet.App;
import org.javastreet.models.HistoryEntry;

import java.sql.*;
import java.util.ArrayList;

public class DBHistory {

    private ArrayList<HistoryEntry> history;
    private Connection connection;

    public DBHistory() {
        connection= App.connection.getConnection();
        history = new ArrayList<>();
        String sql = "CREATE TABLE IF NOT EXISTS History (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + " link text NOT NULL,\n"
                + "	date DATETIME\n"
                + ");";
        App.connection.createTable(sql);
        this.selectAll();
    }

    public void selectAll(){
        String sql = "SELECT * FROM History";

        try {
            Statement stmt  = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                HistoryEntry he = new HistoryEntry(
                        rs.getString("name"),
                        rs.getString("link"),
                        rs.getDate("date")
                );
                System.out.println(he);
                history.add(he);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(HistoryEntry he) {
        String sql = "INSERT INTO History(name,link, date) VALUES(?,?,?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, he.getName());
            pstmt.setString(2, he.getLink());
            pstmt.setDate(3, new Date(he.getDate().getTime()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            history.add(he);
        }
    }

    public ArrayList<HistoryEntry> getHistory() {
        return history;
    }
}

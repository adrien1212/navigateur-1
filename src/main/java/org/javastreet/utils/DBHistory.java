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
                        rs.getInt("id"),
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

    public void insert(HistoryEntry he) throws SQLException {
        int id = -1;
        String sql = "INSERT INTO History(name,link, date) VALUES(?,?,?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, he.getName());
            pstmt.setString(2, he.getLink());
            pstmt.setDate(3, new Date(he.getDate().getTime()));
            pstmt.executeUpdate();
            ResultSet rsKey = pstmt.getGeneratedKeys();
            if (rsKey.next()){
                id = rsKey.getInt(1);
                System.out.println("id : " + id);
            } else {
                throw new SQLException("Erreur dans la récupération de l'ID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            he.setId(id);
            history.add(he);
        }
    }

    public void delete(HistoryEntry he) throws SQLException {
        String sql = "DELETE FROM History WHERE id=?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, he.getId());
            int retour = pstmt.executeUpdate();
            if(retour != 1){
                throw new SQLException("Erreur dans la suppresion");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            //history.remove(he);
            history.clear();
            selectAll();
        }
    }

    public void deleteItems(HistoryEntry... hes) throws SQLException {
        for(HistoryEntry he: hes){
            delete(he);
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM History";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            int retour = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            history.clear();
        }
    }

    public ArrayList<HistoryEntry> getHistory() {
        return history;
    }
}

package com.example.projectedp.service;

import com.example.projectedp.model.Stop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class DatabaseService {
    private static final String URL = "jdbc:sqlite:stops.db";
    private Connection conn;

    public void init() throws SQLException {
        conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
              CREATE TABLE IF NOT EXISTS favorites (
                stop_id   TEXT PRIMARY KEY,
                name      TEXT NOT NULL,
                latitude  REAL,
                longitude REAL,
                added_at  DATETIME DEFAULT CURRENT_TIMESTAMP
              );
            """);
            stmt.execute("""
              CREATE TABLE IF NOT EXISTS search_history (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                query       TEXT NOT NULL,
                searched_at DATETIME DEFAULT CURRENT_TIMESTAMP
              );
            """);
        }
    }

    /** Zapisuje przystanek jako ulubiony */
    public void addFavorite(Stop stop) throws SQLException {
        String sql = """
          INSERT OR IGNORE INTO favorites(stop_id, name, latitude, longitude)
          VALUES (?, ?, ?, ?);
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stop.getId());
            ps.setString(2, stop.getName());
            ps.setDouble(3, stop.getLatitude());
            ps.setDouble(4, stop.getLongitude());
            ps.executeUpdate();
        }
    }

    /** Usuwa przystanek z ulubionych */
    public void removeFavorite(Stop stop) throws SQLException {
        String sql = "DELETE FROM favorites WHERE stop_id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stop.getId());
            ps.executeUpdate();
        }
    }

    /** Pobiera wszystkie ulubione przystanki */
    public List<Stop> getAllFavorites() throws SQLException {
        List<Stop> list = new ArrayList<>();
        String sql = "SELECT stop_id, name, latitude, longitude FROM favorites;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Stop(
                        rs.getString("stop_id"),
                        rs.getString("name"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getString("longitude")
                ));
            }
        }
        return list;
    }

    /** Zapisuje wyszukiwaną frazę w historii */
    public void saveSearchQuery(String query) throws SQLException {
        String sql = "INSERT INTO search_history(query) VALUES (?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query);
            ps.executeUpdate();
        }
    }

    /** Pobiera historię wyszukiwań */
    public List<String> getSearchHistory() throws SQLException {
        List<String> history = new ArrayList<>();
        String sql = "SELECT query FROM search_history ORDER BY searched_at DESC;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                history.add(rs.getString("query"));
            }
        }
        return history;
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }

}

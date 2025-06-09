package com.example.projectedp.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:stops.db";
    private static Connection conn;

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
            initializeTables(conn);
        }
        return conn;
    }

    private static void initializeTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS favorites (
                    stop_id     TEXT PRIMARY KEY,
                    name        TEXT NOT NULL,
                    stop_number TEXT NOT NULL,
                    latitude    REAL,
                    longitude   REAL,
                    added_at    DATETIME DEFAULT CURRENT_TIMESTAMP
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

    public static void close() throws SQLException {
        if (conn != null) conn.close();
    }

}

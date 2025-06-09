package com.example.projectedp.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryDaoImpl implements SearchHistoryDao {
    private static SearchHistoryDaoImpl instance;

    private SearchHistoryDaoImpl() {}

    public static SearchHistoryDaoImpl getInstance() {
        if (instance == null) {
            instance = new SearchHistoryDaoImpl();
        }
        return instance;
    }

    @Override
    public void saveQuery(String query) throws SQLException {
        String sql = "INSERT INTO search_history(query) VALUES (?);";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, query);
            ps.executeUpdate();
        }
    }

    @Override
    public List<String> getHistory() throws SQLException {
        List<String> history = new ArrayList<>();
        String sql = "SELECT query FROM search_history ORDER BY searched_at DESC;";
        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                history.add(rs.getString("query"));
            }
        }
        return history;
    }
}

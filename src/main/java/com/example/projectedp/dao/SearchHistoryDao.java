package com.example.projectedp.dao;

import java.sql.SQLException;
import java.util.List;

public interface SearchHistoryDao {
    void saveQuery(String query) throws SQLException;
    List<String> getHistory() throws SQLException;
}

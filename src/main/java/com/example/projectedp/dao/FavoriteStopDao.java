package com.example.projectedp.dao;

import com.example.projectedp.model.Stop;
import java.sql.SQLException;
import java.util.List;

public interface FavoriteStopDao {
    void add(Stop stop) throws SQLException;
    void remove(Stop stop) throws SQLException;
    List<Stop> getAll() throws SQLException;
}

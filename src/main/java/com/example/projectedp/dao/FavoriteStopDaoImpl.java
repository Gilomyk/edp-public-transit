package com.example.projectedp.dao;


import com.example.projectedp.model.Stop;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoriteStopDaoImpl implements FavoriteStopDao {

    private static FavoriteStopDaoImpl instance;

    private FavoriteStopDaoImpl() {}

    public static FavoriteStopDaoImpl getInstance() {
        if (instance == null) {
            instance = new FavoriteStopDaoImpl();
        }
        return instance;
    }

    @Override
    public void add(Stop stop) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
             INSERT OR REPLACE INTO favorites (stop_id, name, stop_number, latitude, longitude)
             VALUES (?, ?, ?, ?, ?);
         """)) {
            stmt.setString(1, stop.getId());
            stmt.setString(2, stop.getName());
            stmt.setString(3, stop.getStopNumber()); // nowy wiersz
            stmt.setDouble(4, stop.getLatitude());
            stmt.setDouble(5, stop.getLongitude());
            stmt.executeUpdate();
        }
    }


    @Override
    public void remove(Stop stop) throws SQLException {
        String sql = "DELETE FROM favorites WHERE stop_id = ?;";
        try (PreparedStatement ps = DatabaseManager.getConnection().prepareStatement(sql)) {
            ps.setString(1, stop.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Stop> getAll() throws SQLException {
        List<Stop> list = new ArrayList<>();
        String sql = "SELECT stop_id, name, stop_number, latitude, longitude FROM favorites";
        try (Statement stmt = DatabaseManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Stop(
                        rs.getString("stop_id"),
                        rs.getString("name"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getString("stop_number")  // Możesz zmienić, jeśli to błąd
                ));
            }
        }
        return list;
    }
}

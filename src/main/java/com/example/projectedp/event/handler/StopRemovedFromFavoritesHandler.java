package com.example.projectedp.event.handler;

import com.example.projectedp.controller.MainController;
import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.StopRemovedFromFavoritesEvent;
import com.example.projectedp.model.Stop;
import com.example.projectedp.service.DatabaseService;

import java.util.List;
import java.sql.SQLException;

public class StopRemovedFromFavoritesHandler implements EventHandler<StopRemovedFromFavoritesEvent> {
    private final DatabaseService databaseService;
    private final MainController controller;

    public StopRemovedFromFavoritesHandler(DatabaseService databaseService, MainController controller) {
        this.databaseService = databaseService;
        this.controller = controller;
    }

    @Override
    public void handle(StopRemovedFromFavoritesEvent event) {
        try {
            databaseService.removeFavorite(event.getStop());
        List<Stop> updated = databaseService.getAllFavorites();
        controller.updateFavoritesList(updated);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

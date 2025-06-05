package com.example.projectedp.event.handler;

import com.example.projectedp.controller.MainController;
import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.StopAddedToFavoritesEvent;
import com.example.projectedp.model.Stop;
import com.example.projectedp.service.DatabaseService;

import java.sql.SQLException;

public class StopAddedToFavoritesHandler implements EventHandler<StopAddedToFavoritesEvent> {
    private final DatabaseService databaseService;
    private final MainController controller;
    public StopAddedToFavoritesHandler(DatabaseService databaseService, MainController controller) {
        this.databaseService = databaseService;
        this.controller = controller;
    }
    @Override
    public void handle(StopAddedToFavoritesEvent event) {
        Stop stop = event.getStop();
        try {
            databaseService.addFavorite(stop);
            controller.updateFavoritesList(databaseService.getAllFavorites());
        } catch (SQLException e) {
            e.printStackTrace();
            // Możesz wysłać ApiErrorEvent albo pokazać komunikat w UI
        }
    }
}


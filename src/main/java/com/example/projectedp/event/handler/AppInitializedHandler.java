package com.example.projectedp.event.handler;

import com.example.projectedp.controller.MainController;
import com.example.projectedp.event.AppInitializedEvent;
import com.example.projectedp.event.EventHandler;
import com.example.projectedp.model.Stop;
import com.example.projectedp.service.ApiService;
import com.example.projectedp.service.DatabaseService;
import javafx.application.Platform;

import java.sql.SQLException;
import java.util.List;

/**

 Handler reagujący na AppInitializedEvent – ładuje dane początkowe (przystanki, ulubione)
 */
public class AppInitializedHandler implements EventHandler<AppInitializedEvent> {

    private final MainController controller;
    private final ApiService apiService;
    private final DatabaseService databaseService;

    public AppInitializedHandler(MainController controller, ApiService apiService, DatabaseService databaseService) {
        this.controller = controller;
        this.apiService = apiService;
        this.databaseService = databaseService;
    }

    @Override
    public void handle(AppInitializedEvent event) {
// 1. Ładowanie przystanków z API
        System.out.println("Trying to FETCH");
        apiService.fetchStopsAsync();

        // 2. Ładowanie ulubionych z DB
        try {
            List<Stop> favorites = databaseService.getAllFavorites();
            Platform.runLater(() -> controller.updateFavoritesList(favorites));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
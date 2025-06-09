package com.example.projectedp.event.handler;

import com.example.projectedp.controller.MainController;
import com.example.projectedp.dao.FavoriteStopDao;
import com.example.projectedp.dao.FavoriteStopDaoImpl;
import com.example.projectedp.dao.SearchHistoryDao;
import com.example.projectedp.dao.SearchHistoryDaoImpl;
import com.example.projectedp.event.AppInitializedEvent;
import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.Handles;
import com.example.projectedp.model.Stop;
import com.example.projectedp.service.ApiService;
import com.example.projectedp.service.DatabaseService;
import javafx.application.Platform;

import java.sql.SQLException;
import java.util.List;

/**

 Handler reagujący na AppInitializedEvent – ładuje dane początkowe (przystanki, ulubione)
 */
@Handles(AppInitializedEvent.class)
public class AppInitializedHandler implements EventHandler<AppInitializedEvent> {

    private final MainController controller;
    private final ApiService apiService;
    private FavoriteStopDao favoriteStopDao;

    public AppInitializedHandler(MainController controller, ApiService apiService) { // bez databaseService
        this.controller = controller;
        this.apiService = apiService;
        this.favoriteStopDao = FavoriteStopDaoImpl.getInstance();
    }

    @Override
    public void handle(AppInitializedEvent event) {
// 1. Ładowanie przystanków z API
        System.out.println("Trying to FETCH");
        apiService.fetchStopsAsync();

        // 2. Ładowanie ulubionych z DB
        try {
//            List<Stop> favorites = databaseService.getAllFavorites();
            List<Stop> favorites = favoriteStopDao.getAll();
            Platform.runLater(() -> controller.updateFavoritesList(favorites));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
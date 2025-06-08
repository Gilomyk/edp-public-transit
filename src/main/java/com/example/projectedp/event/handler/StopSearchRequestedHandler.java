package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.StopSearchRequestedEvent;
import com.example.projectedp.model.Stop;
import com.example.projectedp.controller.MainController;
import com.example.projectedp.service.ApiService;
import com.example.projectedp.service.DatabaseService;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLException;

public class StopSearchRequestedHandler implements EventHandler<StopSearchRequestedEvent>{

    private final DatabaseService databaseService;
    private final MainController controller;
    private final ApiService apiService;

    public StopSearchRequestedHandler(DatabaseService databaseService, MainController controller, ApiService apiService) {
        this.databaseService = databaseService;
        this.controller = controller;
        this.apiService = apiService;
    }
    @Override
    public void handle(StopSearchRequestedEvent event) {
        String query = event.getQuery().trim().toLowerCase();

        // 1) Zapis do historii w bazie
        if (!query.isEmpty()) {
            try {
                databaseService.saveSearchQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (query.isEmpty()) {
            controller.updateStopList(controller.getAllStops());
            // Na mapie wyświetlamy wszystkie
            controller.plotStopsOnMap(controller.getAllStops());
        } else {
            // 3) Filtrowanie lokalne przystanków
            List<Stop> filtered = controller.getAllStops().stream()
                    .filter(stop -> stop.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());

            controller.updateStopList(filtered);
            controller.plotStopsOnMap(filtered);
        }

        // Aktualizacja listy w GUI
        controller.addRecentSearch(event.getQuery());
    }
}

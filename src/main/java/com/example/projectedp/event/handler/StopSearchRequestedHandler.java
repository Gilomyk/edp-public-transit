package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.StopSearchRequestedEvent;
import com.example.projectedp.model.Stop;
import com.example.projectedp.controller.MainController;
import com.example.projectedp.service.DatabaseService;

import java.util.List;
import java.util.stream.Collectors;

import java.sql.SQLException;

public class StopSearchRequestedHandler implements EventHandler<StopSearchRequestedEvent>{

    private final DatabaseService databaseService;
    private final MainController controller;

    public StopSearchRequestedHandler(DatabaseService databaseService, MainController controller) {
        this.databaseService = databaseService;
        this.controller = controller;
    }
    @Override
    public void handle(StopSearchRequestedEvent event) {
        String query = event.getQuery().toLowerCase();

        List<Stop> filtered;

        if(query.isEmpty()) {
            filtered = controller.getAllStops();
        } else {
            filtered = controller.getAllStops().stream()
                    .filter(stop -> stop.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
            try {
                databaseService.saveSearchQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Aktualizacja listy w GUI
        controller.updateStopList(filtered);
        controller.addRecentSearch(event.getQuery());
    }
}

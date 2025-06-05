package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.StopSearchRequestedEvent;
import com.example.projectedp.model.Stop;
import com.example.projectedp.controller.MainController;

import java.util.List;
import java.util.stream.Collectors;

public class StopSearchRequestedHandler implements EventHandler<StopSearchRequestedEvent>{

    private final MainController controller;

    public StopSearchRequestedHandler(MainController controller) {
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
        }
        // Aktualizacja listy w GUI
        controller.updateStopList(filtered);
    }
}

package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.StopsLoadedEvent;
import com.example.projectedp.model.Stop;
import com.example.projectedp.controller.MainController;
import javafx.application.Platform;

import java.util.List;

/**
 * Handler reagujący na event StopsLoadedEvent – aktualizuje listę przystanków w MainController
 * i rysuje markery na mapie.
 */
public class StopsLoadedHandler implements EventHandler<StopsLoadedEvent> {

    private final MainController controller;

    public StopsLoadedHandler(MainController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(StopsLoadedEvent event) {
        List<Stop> stops = event.getStops();
        Platform.runLater(() -> {
            // Aktualizujemy ListView przystanków
            controller.updateStopList(stops);
            // Rysujemy markery na mapie
            controller.plotStopsOnMap(stops);
        });
    }
}

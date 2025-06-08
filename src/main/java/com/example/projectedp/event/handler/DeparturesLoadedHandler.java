package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.DeparturesLoadedEvent;
import com.example.projectedp.model.Departure;
import com.example.projectedp.controller.MainController;
import javafx.application.Platform;

import java.util.Comparator;
import java.util.List;

/**
 * Handler reagujący na zdarzenie pobrania listy odjazdów.
 * Aktualizuje GUI (ListView odjazdów).
 */
public class DeparturesLoadedHandler implements EventHandler<DeparturesLoadedEvent> {

    private final MainController controller;

    public DeparturesLoadedHandler(MainController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(DeparturesLoadedEvent event) {
        List<Departure> departures = event.getDepartures();

        departures.sort(Comparator.comparing(Departure::getTime));

        // GUI musi być zaktualizowane na wątku JavaFX
        Platform.runLater(() -> {
            controller.updateDepartureList(departures);
        });
    }
}

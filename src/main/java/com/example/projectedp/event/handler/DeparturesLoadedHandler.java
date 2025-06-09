package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.DeparturesLoadedEvent;
import com.example.projectedp.event.Handles;
import com.example.projectedp.model.Departure;
import com.example.projectedp.controller.MainController;
import com.example.projectedp.model.Line;
import javafx.application.Platform;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handler reagujący na zdarzenie pobrania listy odjazdów.
 * Aktualizuje GUI (ListView odjazdów).
 */
@Handles(DeparturesLoadedEvent.class)
public class DeparturesLoadedHandler implements EventHandler<DeparturesLoadedEvent> {

    private final MainController controller;

    public DeparturesLoadedHandler(MainController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(DeparturesLoadedEvent event) {
        List<Departure> departures = event.getDepartures();
        LocalDateTime now = LocalDateTime.now();

        // Filtrowanie: tylko przyszłe lub bieżące odjazdy
        List<Departure> upcoming = departures.stream()
                .filter(d -> !d.getTime().isBefore(now))
                .sorted(Comparator.comparing(Departure::getTime))
                .limit(15)
                .toList();

        // Jeśli nie ma żadnych odjazdów, dodaj wiadomość tekstową jako placeholder
        if (upcoming.isEmpty()) {
            Platform.runLater(() -> {
                controller.updateDepartureList(Collections.emptyList());
                controller.getDepartureList().getItems().add(
                        new Departure("N/A", "—", "Brak nadchodzących odjazdów", now)
                );
            });
            return;
        }

        // Budowanie mapy: linia → zbiór kierunków
        Map<String, Set<String>> lineToDirections = new TreeMap<>();
        for (Departure d : departures) {
            lineToDirections
                    .computeIfAbsent(d.getLine(), k -> new TreeSet<>())
                    .add(d.getDestination());
        }

        // Tworzenie listy Line
        List<Line> enrichedLines = departures.stream()
                .map(d -> new Line(d.getLine(), d.getDestination()))
                .distinct() // usuwa duplikaty bazując na equals()
                .toList();


        Platform.runLater(() -> {
            controller.updateDepartureList(upcoming);
            controller.updateLineList(new ArrayList<>(enrichedLines));
        });
    }

}

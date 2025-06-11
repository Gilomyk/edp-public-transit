package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.DeparturesLoadedEvent;
import com.example.projectedp.event.Handles;
import com.example.projectedp.model.Departure;
import com.example.projectedp.controller.MainController;
import com.example.projectedp.model.Line;
import javafx.application.Platform;

import java.time.Duration;
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

        // Wyznaczenie odjazdów w najbliższych 60 minutach
        List<Departure> upcoming = departures.stream()
                .filter(d -> {
                    long diff = Duration.between(now, d.getTime()).toMinutes();
                    return diff >= 0 && diff <= 60;
                })
                .sorted(Comparator.comparing(Departure::getTime))
                .toList();

        // Mapowanie: linia → zbiór kierunków (dla wyświetlania linii)
        Map<String, Set<String>> lineToDirections = new TreeMap<>();
        for (Departure d : departures) { // <- pełna lista, nie tylko upcoming
            lineToDirections
                    .computeIfAbsent(d.getLine(), k -> new TreeSet<>())
                    .add(d.getDestination());
        }

        // Lista unikalnych linii (z dowolnym przykładowym kierunkiem)
        List<Line> enrichedLines = lineToDirections.entrySet().stream()
                .map(entry -> new Line(entry.getKey(), entry.getValue().iterator().next()))
                .toList();

        // Aktualizacja GUI
        Platform.runLater(() -> {
            if (upcoming.isEmpty()) {
                controller.updateDepartureList(Collections.emptyList());
                controller.getDepartureList().getItems().add(
                        new Departure("N/A", "—", "Brak nadchodzących odjazdów", now)
                );
            } else {
                controller.updateDepartureList(upcoming);
            }

            controller.updateLineList(new ArrayList<>(enrichedLines));
        });
    }
}


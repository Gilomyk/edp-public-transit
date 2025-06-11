package com.example.projectedp.ui;

import com.example.projectedp.model.Departure;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DepartureTimelineView extends Region {

    private static final double WIDTH = 600;     // szerokość osi
    private static final double HEIGHT = 100;    // wysokość komponentu
    private static final double MARGIN = 40;     // margines z lewej i prawej
    private static final int MAX_DEPARTURES = 10;
    private static final int MAX_MINUTES = 60;   // maksymalny zakres czasu na osi w minutach

    private Pane content;

    private List<Departure> departures;
    private LocalDateTime now;

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public DepartureTimelineView() {
        this.setPrefSize(WIDTH, HEIGHT);
        content = new Pane();
        this.getChildren().add(content);
    }

    public void setDepartures(List<Departure> departures) {
        this.now = LocalDateTime.now();

        // Jeśli lista pusta, to nic nie rysuj
        if (departures.isEmpty()) {
            this.departures = Collections.emptyList();
            drawTimeline();
            return;
        }

        // Oblicz MAX_MINUTES jako różnicę między teraz a ostatnim odjazdem (w minutach)
        LocalDateTime lastDepartureTime = departures.get(departures.size() - 1).getTime();
        long maxMinutes = java.time.Duration.between(now, lastDepartureTime).toMinutes();

        // Filtruj odjazdy mieszczące się w maxMinutes
        List<Departure> filtered = departures.stream()
                .filter(d -> {
                    long diff = java.time.Duration.between(now, d.getTime()).toMinutes();
                    return diff >= 0 && diff <= maxMinutes;
                })
                .collect(Collectors.toList());

        // Ogranicz do MAX_DEPARTURES
        if (filtered.size() > MAX_DEPARTURES) {
            this.departures = filtered.subList(0, MAX_DEPARTURES);
        } else {
            this.departures = filtered;
        }

        drawTimeline();
    }


    private void drawTimeline() {
        content.getChildren().clear();

        // linia osi czasu
        Line timeline = new Line(MARGIN, HEIGHT / 2, WIDTH - MARGIN, HEIGHT / 2);
        timeline.setStroke(Color.DARKGRAY);
        timeline.setStrokeWidth(2);
        content.getChildren().add(timeline);

        if (departures == null || departures.isEmpty()) {
            return;
        }

        for (Departure d : departures) {
            long minutesFromNow = Duration.between(now, d.getTime()).toMinutes();

            // ograniczamy do zakresu osi czasu (0 - MAX_MINUTES)
            if (minutesFromNow < 0) minutesFromNow = 0;
            if (minutesFromNow > MAX_MINUTES) minutesFromNow = MAX_MINUTES;

            // Obliczamy pozycję X na osi
            double x = MARGIN + (minutesFromNow / (double) MAX_MINUTES) * (WIDTH - 2 * MARGIN);

            // Tworzymy kółko
            Circle circle = new Circle(x, HEIGHT / 2, 8);
            circle.setFill(Color.CORNFLOWERBLUE);
            circle.setStroke(Color.DARKBLUE);
            circle.setStrokeWidth(1.5);

            // Etykieta z godziną odjazdu
            Label timeLabel = new Label(d.getTime().format(timeFormatter));
            timeLabel.setLayoutX(x - 20);
            timeLabel.setLayoutY(HEIGHT / 2 - 35);
            timeLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

            // Etykieta z numerem linii
            Label lineLabel = new Label(d.getLine());
            lineLabel.setLayoutX(x - 10);
            lineLabel.setLayoutY(HEIGHT / 2 + 15);
            lineLabel.setStyle("-fx-font-size: 12px; -fx-background-color: #3399FF; -fx-text-fill: white; -fx-padding: 2 6 2 6; -fx-background-radius: 4;");

            content.getChildren().addAll(circle, timeLabel, lineLabel);
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        content.resizeRelocate(0, 0, getWidth(), getHeight());
    }
}

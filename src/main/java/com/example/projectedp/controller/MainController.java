package com.example.projectedp.controller;

import com.example.projectedp.event.*;
import com.example.projectedp.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<Stop> stopList;

    @FXML
    private ListView<Departure> departureList;

    @FXML
    private Button addToFavoritesButton;

    @FXML
    private Button notifyButton;

    private EventBus eventBus;

    private final List<Stop> allStops = new ArrayList<>();

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @FXML
    public void initialize() {
        // Przykładowe przystanki
        allStops.add(new Stop("1", "Rondo Solidarności", 50.0, 20.0));
        allStops.add(new Stop("2", "Dworzec", 50.1, 20.1));
        allStops.add(new Stop("3", "Piłsudskiego", 50.2, 20.2));

        // Inicjalizacja listy
        stopList.getItems().addAll(allStops);

        departureList.getItems().addAll(
                new Departure("d001", "Linia 5", "Centrum", LocalDateTime.of(2025, 6, 5, 14, 30)),
                new Departure("d002", "Linia 8", "Dworzec", LocalDateTime.of(2025, 6, 5, 14, 45))
        );

        // Dodanie do ulubionych
        addToFavoritesButton.setOnAction(event -> {
            Stop selectedStop = stopList.getSelectionModel().getSelectedItem();
            if (selectedStop != null) {
                eventBus.post(new StopAddedToFavoritesEvent(selectedStop));
            }
        });

        // Obsługa przycisku "Szukaj"
        searchButton.setOnAction(event -> {
            String query = searchField.getText();
            if (query != null) {
                eventBus.post(new StopSearchRequestedEvent(query));
            }
        });

        // Obsługa wyboru przystanku z listy (kliknięcie w item)
        stopList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                eventBus.post(new StopSelectedEvent(newVal));
            }
        });

        // Obsługa kliknięcia "Powiadom o kursie" – opcjonalnie
        notifyButton.setOnAction(event -> eventBus.post(new NotificationRequestedEvent("Kurs się zbliża!")));
    }

    public List<Stop> getAllStops() {
        return allStops;
    }

    // Metoda wywoływana przez handler, aby zaktualizować widok przystanków
    public void updateStopList(List<Stop> filteredStops) {
        stopList.getItems().setAll(filteredStops);
    }
}
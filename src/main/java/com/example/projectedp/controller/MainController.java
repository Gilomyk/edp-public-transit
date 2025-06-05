package com.example.projectedp.controller;

import com.example.projectedp.event.*;
import com.example.projectedp.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    private Button removeFromFavoritesButton;

    @FXML
    private Button notifyButton;

    @FXML
    private ListView<Stop> favoritesList;

    @FXML
    public void handleShowAllStops() {
        stopList.getItems().setAll(allStops);
    }

    private final LinkedList<RecentSearch> recentSearches = new LinkedList<>();

    @FXML
    private ListView<RecentSearch> recentSearchesList;

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

        favoritesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                eventBus.post(new StopSelectedEvent(newVal));
            }
        });

        // Usuwanie z polubionych
        removeFromFavoritesButton.setOnAction(event -> {
            Stop selectedStop = favoritesList.getSelectionModel().getSelectedItem();
            if (selectedStop != null) {
                eventBus.post(new StopRemovedFromFavoritesEvent(selectedStop));
            }
        });

        searchField.setOnMouseClicked(event -> showRecentSearches());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                showRecentSearches();
            } else {
                hideRecentSearches();
            }
        });

        // Pokazywanie listy wyszukiwań
        recentSearchesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                searchField.setText(newVal.getQuery());
                hideRecentSearches();
                eventBus.post(new StopSearchRequestedEvent(newVal.getQuery()));
            }
        });

    }

    public List<Stop> getAllStops() {
        return allStops;
    }

    // Aktualizacja widoku przystanków
    public void updateStopList(List<Stop> filteredStops) {
        stopList.getItems().setAll(filteredStops);
    }


    // Aktaulizacja widoku polubionych przystanków
    public void updateFavoritesList(List<Stop> favorites) {
        // Zakładamy, że masz osobny ListView na ulubione przystanki — np. favoritesListView
        Platform.runLater(() -> {
            favoritesList.getItems().setAll(favorites);
        });
    }

    private void showRecentSearches() {
        if (!recentSearches.isEmpty()) {
            recentSearchesList.setVisible(true);
            recentSearchesList.getItems().setAll(recentSearches);
        }
    }

    private void hideRecentSearches() {
        recentSearchesList.setVisible(false);
    }


    public void addRecentSearch(String query) {
        Optional<RecentSearch> existing = recentSearches.stream()
                .filter(s -> s.getQuery().equalsIgnoreCase(query))
                .findFirst();

        existing.ifPresent(recentSearches::remove);
        recentSearches.addFirst(new RecentSearch(query, LocalDateTime.now()));

        if (recentSearches.size() > 10) {
            recentSearches.removeLast();
        }

        // Automatycznie uaktualnij widok
        Platform.runLater(() -> {
            if (recentSearchesList.isVisible()) {
                recentSearchesList.getItems().setAll(recentSearches);
            }
        });
    }


}
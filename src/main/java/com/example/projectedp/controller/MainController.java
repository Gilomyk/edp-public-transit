package com.example.projectedp.controller;

import com.example.projectedp.MainApp;
import com.example.projectedp.event.*;
import com.example.projectedp.model.*;
import com.example.projectedp.service.*;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<Stop> stopList;
    @FXML private ListView<Departure> departureList;
    @FXML private ListView<Line> lineList;
    @FXML private Button addToFavoritesButton;
    @FXML private Button removeFromFavoritesButton;
    @FXML private Button notifyButton;
    @FXML private ListView<Stop> favoritesList;
    @FXML private ListView<RecentSearch> recentSearchesList;
    @FXML private WebView mapView;

    @FXML public void handleShowAllStops() {
        stopList.getItems().setAll(allStops);
    }

    private List<Stop> allStops;
    private final LinkedList<RecentSearch> recentSearches = new LinkedList<>();
    private JSObject jsBridge;
    private EventBus eventBus;
    private DatabaseService databaseService;
    private ApiService apiService;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
    public void setDatabaseService(DatabaseService databaseService) { this.databaseService = databaseService; }
    public void setApiService(ApiService apiService) { this.apiService = apiService; }


    @FXML
    public void initialize() {

        // Inicjalizacja WebView
        WebEngine webEngine = mapView.getEngine();
        webEngine.load(MainApp.class.getResource("view/map.html").toExternalForm());

        // Ustawianie jsBridge
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                jsBridge = (JSObject) webEngine.executeScript("window");
                jsBridge.setMember("app", this);
            }
        });

        webEngine.setOnError(event -> {
            System.out.println("JS Error: " + event.getMessage());
        });
        webEngine.setOnAlert(event -> {
            System.out.println("JS Alert: " + event.getData());
        });
        webEngine.setOnStatusChanged(event -> {
            System.out.println("JS Status: " + event.getData());
        });

        // Dodanie do ulubionych
        addToFavoritesButton.setOnAction(event -> {
            Stop selectedStop = stopList.getSelectionModel().getSelectedItem();
            if (selectedStop != null) {
                try {
                    databaseService.addFavorite(selectedStop);
                    this.updateFavoritesList(databaseService.getAllFavorites());
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Możesz wysłać ApiErrorEvent albo pokazać komunikat w UI
                }
            }
        });

        // Usuwanie z polubionych
        removeFromFavoritesButton.setOnAction(event -> {
            Stop selectedStop = favoritesList.getSelectionModel().getSelectedItem();
            if (selectedStop != null) {
                try {
                    databaseService.removeFavorite(selectedStop);
                    List<Stop> updated = databaseService.getAllFavorites();
                    this.updateFavoritesList(updated);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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

        // Przypadek dla polubionych
        favoritesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                eventBus.post(new StopSelectedEvent(newVal));
            }
        });

        // Obsługa kliknięcia "Powiadom o kursie" – opcjonalnie
        notifyButton.setOnAction(event -> {
            Stop selectedStop = stopList.getSelectionModel().getSelectedItem();
            if (selectedStop != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Powiadomienie");
                alert.setHeaderText(null);
                alert.setContentText("Powiadomienie o przystanku: " + selectedStop.getName());
                alert.showAndWait();
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

        Platform.runLater(() -> {
            eventBus.post(new AppInitializedEvent());
        });

    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }


    public List<Stop> getAllStops() {
        return allStops;
    }

    // Aktualizacja widoku przystanków
    public void updateStopList(List<Stop> stops) {
        this.allStops = stops;
        stopList.getItems().setAll(stops);
    }

    // Aktaulizacja widoku polubionych przystanków
    public void updateFavoritesList(List<Stop> favorites) {
        // Zakładamy, że masz osobny ListView na ulubione przystanki — np. favoritesListView
        Platform.runLater(() -> {
            favoritesList.getItems().setAll(favorites);
        });
    }

    public void updateDepartureList(List<Departure> departures) {
        departureList.getItems().setAll(departures);
    }

    public void updateLineList(List<Line> lines) {
        lineList.getItems().setAll(lines);
    }



    /** Rysuje markery dla listy przystanków na mapie */
    public void plotStopsOnMap(List<Stop> stops) {
        // 1. Najpierw usuń istniejące markery
        jsBridge.call("clearMarkers");
        // 2. Dodaj nowe markery
        for (Stop s : stops) {
            String jsCode = String.format(
                    "addMarker(%f, %f, '%s', '%s')",
                    s.getLatitude(), s.getLongitude(), s.getId(), s.getName().replace("'", "\\'")
            );
            jsBridge.call("eval", jsCode);
        }
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
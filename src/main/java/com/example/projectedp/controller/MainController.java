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
import java.util.*;

public class MainController {

    // --- UI komponenty ---
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<Stop> stopList;
    @FXML private ListView<Departure> departureList;
    @FXML private ListView<Line> lineList;
    @FXML private Button addToFavoritesButton;
    @FXML private Button removeFromFavoritesButton;
    @FXML private Button getLinesButton;
    @FXML private Button getDeparturesButton;
    @FXML private Button notifyButton;
    @FXML private ListView<Stop> favoritesList;
    @FXML private ListView<RecentSearch> recentSearchesList;
    @FXML private WebView mapView;

    // --- Pola pomocnicze ---
    private final List<Stop> allStops = new ArrayList<>();
    private final LinkedList<RecentSearch> recentSearches = new LinkedList<>();
    private JSObject jsBridge;
    private boolean mapInitialized = false;
    private EventBus eventBus;
    private DatabaseService databaseService;
    private ApiService apiService;

    // --- Wstrzykiwanie zależności ---
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    @FXML
    public void initialize() {
        initMap();
        initButtonHandlers();
        initListSelectionHandlers();
        initSearchHandling();

        Platform.runLater(() -> eventBus.post(new AppInitializedEvent()));
    }

    // --- Inicjalizacja mapy ---
    private void initMap() {
        WebEngine webEngine = mapView.getEngine();
        webEngine.load(MainApp.class.getResource("view/map.html").toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                jsBridge = (JSObject) webEngine.executeScript("window");
                jsBridge.setMember("app", this);
            }
        });

        webEngine.setOnError(e -> System.out.println("JS Error: " + e.getMessage()));
        webEngine.setOnAlert(e -> System.out.println("JS Alert: " + e.getData()));
        webEngine.setOnStatusChanged(e -> System.out.println("JS Status: " + e.getData()));
    }

    // --- Obsługa przycisków ---
    private void initButtonHandlers() {
        searchButton.setOnAction(event -> {
            String query = searchField.getText();
            if (query != null && !query.isBlank()) {
                eventBus.post(new StopSearchRequestedEvent(query));
            }
        });

        getLinesButton.setOnAction(event -> {
            Stop selectedStop = stopList.getSelectionModel().getSelectedItem();
            if (selectedStop != null) {
                apiService.fetchLinesAsync(selectedStop.getId(), selectedStop.getStopNumber());
            } else {
                System.out.println("⚠️ Wybierz przystanek, aby pobrać linie.");
            }
        });

        getDeparturesButton.setOnAction(event -> {
            Stop selectedStop = stopList.getSelectionModel().getSelectedItem();
            Line selectedLine = lineList.getSelectionModel().getSelectedItem();
            if (selectedStop != null && selectedLine != null) {
                apiService.fetchDeparturesAsync(
                        selectedStop.getId(),
                        selectedStop.getStopNumber(),
                        selectedLine.getLineNumber()
                );
            } else {
                System.out.println("⚠️ Wybierz przystanek i linię, aby pobrać odjazdy.");
            }
        });

        addToFavoritesButton.setOnAction(event -> {
            Stop selectedStop = stopList.getSelectionModel().getSelectedItem();
            if (selectedStop != null) {
                try {
                    databaseService.addFavorite(selectedStop);
                    updateFavoritesList(databaseService.getAllFavorites());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        removeFromFavoritesButton.setOnAction(event -> {
            Stop selectedStop = favoritesList.getSelectionModel().getSelectedItem();
            if (selectedStop != null) {
                try {
                    databaseService.removeFavorite(selectedStop);
                    updateFavoritesList(databaseService.getAllFavorites());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

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
    }

    // --- Obsługa list wyboru ---
    private void initListSelectionHandlers() {
        stopList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                eventBus.post(new StopSelectedEvent(newVal));
            }
        });

        favoritesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                eventBus.post(new StopSelectedEvent(newVal));
            }
        });

        recentSearchesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                searchField.setText(newVal.getQuery());
                hideRecentSearches();
                eventBus.post(new StopSearchRequestedEvent(newVal.getQuery()));
            }
        });
    }

    // --- Obsługa wyszukiwania i historii ---
    private void initSearchHandling() {
        searchField.setOnMouseClicked(event -> showRecentSearches());

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                showRecentSearches();
            } else {
                hideRecentSearches();
            }
        });
    }

    // --- Pomocnicze metody widoku ---
    public void updateStopList(List<Stop> stops) {
        if (allStops.isEmpty()) {
            allStops.addAll(stops); // tylko raz zapisujemy
        }

        List<Stop> formatted = stops.stream().map(stop -> {
            if (stop.getStopNumber() != null && !stop.getStopNumber().isBlank()) {
                return new Stop(
                        stop.getId(),
                        stop.getName() + " " + stop.getStopNumber(),
                        stop.getLatitude(),
                        stop.getLongitude(),
                        stop.getStopNumber()
                );
            }
            return stop;
        }).toList();

        stopList.getItems().setAll(formatted);
    }

    public void updateFavoritesList(List<Stop> favorites) {
        Platform.runLater(() -> favoritesList.getItems().setAll(favorites));
    }

    public void updateDepartureList(List<Departure> departures) {
        departureList.getItems().setAll(departures);
    }

    public void updateLineList(List<Line> lines) {
        lineList.getItems().setAll(lines);
    }

    public void addRecentSearch(String query) {
        recentSearches.removeIf(s -> s.getQuery().equalsIgnoreCase(query));
        recentSearches.addFirst(new RecentSearch(query, LocalDateTime.now()));

        if (recentSearches.size() > 10) {
            recentSearches.removeLast();
        }

        if (recentSearchesList.isVisible()) {
            Platform.runLater(() -> recentSearchesList.getItems().setAll(recentSearches));
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

    // --- Mapowe ---
    public void onMapInitialized() {
        this.mapInitialized = true;
        System.out.println("Mapa została zainicjalizowana");
    }

    public boolean isMapInitialized() {
        return mapInitialized;
    }

    public void setMapInitialized(boolean value) {
        this.mapInitialized = value;
    }

    public void plotStopsOnMap(List<Stop> stops) {
        if (!mapInitialized) {
            System.out.println("Mapa jeszcze nie zainicjalizowana, pomijam plotStopsOnMap");
            return;
        }

        jsBridge.call("clearMarkers");
        for (Stop s : stops) {
            String jsCode = String.format(
                    "addMarker(%f, %f, '%s', '%s')",
                    s.getLatitude(), s.getLongitude(),
                    s.getId(),
                    s.getName().replace("'", "\\'")
            );
            jsBridge.call("eval", jsCode);
        }
    }

    // --- Pomocnicze ---
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

    @FXML public void handleShowAllStops() {
        // Zarezerwowane na przyszłość
    }
}

package com.example.projectedp.controller;

import com.example.projectedp.MainApp;
import com.example.projectedp.event.*;
import com.example.projectedp.model.*;
import com.example.projectedp.service.*;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class MainController {

    // --- UI komponenty ---
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private ListView<Stop> stopList;
    @FXML private ListView<Stop> favoritesList;
    @FXML private ListView<Departure> departureList;
    @FXML private ListView<Line> lineList;
    @FXML private Button addToFavoritesButton;
    @FXML private Button removeFromFavoritesButton;
    @FXML private Button getLinesButton;
    @FXML private Button showFavoritesButton;
    @FXML private Button getDeparturesButton;
    @FXML private Button notifyButton;
    @FXML private ListView<RecentSearch> recentSearchesList;
    @FXML private WebView mapView;
    @FXML private Label stopListLabel;

    // --- Pola pomocnicze ---
    private final List<Stop> allStops = new ArrayList<>();
    private List<Departure> allDepartures = new ArrayList<>();

    private final LinkedList<RecentSearch> recentSearches = new LinkedList<>();
    private JSObject jsBridge;
    private boolean mapInitialized = false;
    private EventBus eventBus;
    private DatabaseService databaseService;
    private ApiService apiService;
    private Boolean showFavorites = false;

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
        initDepartureListView();
        initLineListView();

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
//                webEngine.executeScript("addMarker(52.2297, 21.0122, '1', 'Warszawa')");
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
            Stop selectedFavouriteStop = favoritesList.getSelectionModel().getSelectedItem();
            if (selectedStop != null) {
                apiService.fetchLinesAsync(selectedStop.getId(), selectedStop.getStopNumber());
            } else {
                System.out.println("⚠️ Wybierz przystanek, aby pobrać linie.");
            }
        });

        getDeparturesButton.setOnAction(e -> updateDepartureList(allDepartures));

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

        showFavoritesButton.setOnAction(event -> toggleStopList());

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

        lineList.setOnMouseClicked(event -> {
            Line selectedLine = lineList.getSelectionModel().getSelectedItem();
            if (selectedLine != null) {
                List<Departure> filtered = allDepartures.stream()
                        .filter(d ->
                                d.getLine().equals(selectedLine.getLineNumber()) &&
                                        d.getDestination().equals(selectedLine.getDirection()))
                        .sorted(Comparator.comparing(Departure::getTime))
                        .toList();

                departureList.getItems().setAll(filtered);
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

    private void initDepartureListView() {
        departureList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Departure departure, boolean empty) {
                super.updateItem(departure, empty);
                if (empty || departure == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("component/DepartureCard.fxml"));
                        HBox card = loader.load();

                        DepartureCardController controller = loader.getController();
                        controller.setData(departure.getLine(), departure.getDestination(), departure.getTime());

                        setGraphic(card);
                    } catch (IOException e) {
                        e.printStackTrace();
                        setGraphic(new Label("Błąd ładowania widoku"));
                    }
                }
            }
        });
    }

    private void initLineListView() {
        lineList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Line line, boolean empty) {
                super.updateItem(line, empty);
                if (empty || line == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("component/LineCard.fxml"));
                        HBox card = loader.load();

                        LineCardController controller = loader.getController();
                        controller.setData(line);

                        setGraphic(card);
                    } catch (IOException e) {
                        e.printStackTrace();
                        setGraphic(new Label("Błąd ładowania widoku"));
                    }
                }
            }
        });
    }


    public ListView<Departure> getDepartureList() {
        return departureList;
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
        this.allDepartures = new ArrayList<>(departures); // zachowujemy oryginał
        departureList.getItems().setAll(departures);
    }


    public void updateLineList(List<Line> lines) {
        lineList.getItems().setAll(lines);
    }

    private void toggleStopList() {
        showFavorites = !showFavorites;

        stopList.setVisible(!showFavorites);
        stopList.setManaged(!showFavorites);

        favoritesList.setVisible(showFavorites);
        favoritesList.setManaged(showFavorites);

        addToFavoritesButton.setVisible(!showFavorites);
        addToFavoritesButton.setManaged(!showFavorites);

        removeFromFavoritesButton.setVisible(showFavorites);
        removeFromFavoritesButton.setManaged(showFavorites);

        stopListLabel.setText(showFavorites ? "Ulubione przystanki" : "Przystanki");
        showFavoritesButton.setText(showFavorites ? "Pokaż wszystkie" : "Pokaż ulubione");
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

        Platform.runLater(() -> {
            WebEngine webEngine = mapView.getEngine();
            webEngine.executeScript("clearMarkers();");
            for (Stop s : stops) {
                String jsCode = String.format(Locale.US,
                        "addMarker(%f, %f, '%s', '%s');",
                        s.getLatitude(),
                        s.getLongitude(),
                        s.getId(),
                        s.getName().replace("'", "\\'")
                );
//                System.out.println(jsCode);
                webEngine.executeScript(jsCode);
            }
        });
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

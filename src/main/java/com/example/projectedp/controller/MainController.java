package com.example.projectedp.controller;

import com.example.projectedp.event.EventBus;
import com.example.projectedp.event.StopAddedToFavoritesEvent;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private EventBus eventBus;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<String> stopList;

    @FXML
    private ListView<String> departureList;

    @FXML
    private Button addToFavoritesButton;

    @FXML
    private Button notifyButton;

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @FXML
    public void initialize() {
        stopList.getItems().addAll("Rondo Solidarności", "Dworzec", "Piłsudskiego");
        departureList.getItems().addAll("Linia 5 → Centrum", "Linia 8 → Dworzec");

        // Obsługa kliknięcia
        addToFavoritesButton.setOnAction(event -> {
            String selected = stopList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                eventBus.post(new StopAddedToFavoritesEvent(selected));
            }
        });
    }

    public static class MainApp extends Application {
        @Override
        public void start(Stage stage) throws IOException {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/main.fxml"));
            Parent root = loader.load();

            // Przekazanie EventBus do kontrolera
            MainController controller = loader.getController();
            EventBus eventBus = new EventBus();
            controller.setEventBus(eventBus);

            // Rejestracja handlerów
    //        eventBus.register(StopSearchRequestedEvent.class, new StopSearchRequestedHandler());
    //        eventBus.register(StopAddedToFavoritesEvent.class, new FavoritesEventHandler());

            stage.setTitle("Rozkład jazdy 3000");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        }

        public static void main(String[] args) {
            launch();
        }
    }
}
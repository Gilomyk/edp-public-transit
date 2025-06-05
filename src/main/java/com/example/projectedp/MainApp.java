package com.example.projectedp;

import com.example.projectedp.controller.MainController;
import com.example.projectedp.event.EventBus;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
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
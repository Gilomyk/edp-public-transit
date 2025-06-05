package com.example.projectedp;

import com.example.projectedp.controller.MainController;
import com.example.projectedp.event.*;
import com.example.projectedp.event.handler.*;
import com.example.projectedp.service.*;
import com.example.projectedp.model.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;

import java.io.IOException;
import java.util.List;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/main.fxml"));
        Parent root = loader.load();

        DatabaseService db = new DatabaseService();
        db.init();

        // Przekazanie EventBus do kontrolera
        MainController controller = loader.getController();
        EventBus eventBus = new EventBus();
        controller.setEventBus(eventBus);

        List<Stop> favs = db.getAllFavorites();
        controller.updateFavoritesList(favs);

        // Rejestracja handlerów
        eventBus.register(DeparturesLoadedEvent.class, new DeparturesLoadedHandler());
        eventBus.register(NotificationRequestedEvent.class, new NotificationRequestedHandler());
        eventBus.register(StopSelectedEvent.class, new StopSelectedHandler());
        eventBus.register(ApiErrorEvent.class, new ApiErrorHandler());
        eventBus.register(StopSearchRequestedEvent.class, new StopSearchRequestedHandler(db, controller));
        eventBus.register(StopAddedToFavoritesEvent.class, new StopAddedToFavoritesHandler(db, controller));
        eventBus.register(StopRemovedFromFavoritesEvent.class, new StopRemovedFromFavoritesHandler(db, controller));

        stage.setTitle("Rozkład jazdy 3000");
        stage.setScene(new Scene(root, 1000, 600));
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
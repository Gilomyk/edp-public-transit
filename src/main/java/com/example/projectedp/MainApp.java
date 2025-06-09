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

        EventBus eventBus = new EventBus();

        ApiService apiService = new ApiService(eventBus);

        // Przekazanie EventBus do kontrolera
        MainController controller = loader.getController();
        controller.setEventBus(eventBus);
        controller.setDatabaseService(db);
        controller.setApiService(apiService);

        List<Stop> favs = db.getAllFavorites();
        controller.updateFavoritesList(favs);

        // Rejestracja handlerów
        // 1) StopSearchRequestedEvent → StopSearchRequestedHandler (filtracja + zapis w bazie + cache)
        eventBus.register(StopSearchRequestedEvent.class,
                new StopSearchRequestedHandler(db, controller, apiService));

        // 2) StopSelectedEvent → StopSelectedHandler (wywołuje pobranie odjazdów)
                eventBus.register(StopSelectedEvent.class,
                        new StopSelectedHandler(apiService, controller));

        // 3) StopsLoadedEvent → StopsLoadedHandler (aktualizacja listy i mapy przystanków)
                eventBus.register(StopsLoadedEvent.class,
                        new StopsLoadedHandler(controller));

        // 4) DeparturesLoadedEvent → DeparturesLoadedHandler (aktualizacja listy odjazdów)
                eventBus.register(DeparturesLoadedEvent.class,
                        new DeparturesLoadedHandler(controller));

        // 5) ApiErrorEvent → ApiErrorHandler (pokazywanie alertów)
                eventBus.register(ApiErrorEvent.class,
                        new ApiErrorHandler());

        // 6) Inicjalizacja danych
                eventBus.register(AppInitializedEvent.class, new AppInitializedHandler(controller, apiService, db));

        eventBus.register(LinesLoadedEvent.class,
                new LinesLoadedHandler(apiService, controller));

        stage.setTitle("Rozkład jazdy 3000");
        Scene scene = new Scene(root, 1600, 800);

        scene.getStylesheets().add(getClass().getResource("/com/example/projectedp/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
package com.example.projectedp;

import com.example.projectedp.controller.MainController;
import com.example.projectedp.dao.DatabaseManager;
import com.example.projectedp.dao.FavoriteStopDao;
import com.example.projectedp.dao.FavoriteStopDaoImpl;
import com.example.projectedp.dao.SearchHistoryDaoImpl;
import com.example.projectedp.event.EventBus;
import com.example.projectedp.model.Stop;
import com.example.projectedp.service.ApiService;
import com.example.projectedp.util.EventHandlerLoader;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/main.fxml"));
        Parent root = loader.load();
        DatabaseManager.getConnection();

        FavoriteStopDaoImpl.getInstance();
        SearchHistoryDaoImpl.getInstance();

        EventBus eventBus = new EventBus();

        ApiService apiService = new ApiService(eventBus);

        MainController controller = loader.getController();
        controller.setEventBus(eventBus);
        controller.setApiService(apiService);

        FavoriteStopDao favoriteStopDao = FavoriteStopDaoImpl.getInstance();
        List<Stop> favorites = favoriteStopDao.getAll();
        Platform.runLater(() -> controller.updateFavoritesList(favorites));

        Map<Class<?>, Object> dependencies = Map.of(
                MainController.class, controller,
                ApiService.class, apiService
        );

        EventHandlerLoader.loadHandlers(eventBus, "com.example.projectedp", dependencies);

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
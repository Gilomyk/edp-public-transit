package com.example.projectedp.event.handler;

import com.example.projectedp.event.ApiErrorEvent;
import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.Handles;
import javafx.application.Platform;
import javafx.scene.control.Alert;

@Handles(ApiErrorEvent.class)
public class ApiErrorHandler implements EventHandler<ApiErrorEvent> {

    @Override
    public void handle(ApiErrorEvent event) {
        String msg = event.getMessage();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd API");
            alert.setHeaderText("Wystąpił błąd przy pobieraniu danych");
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }
}

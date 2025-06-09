package com.example.projectedp.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class DepartureCardController {
    @FXML private Text lineText;
    @FXML private Text destinationText;
    @FXML private Text departureTimeText;
    @FXML private Text countdownText;

    public void setData(String line, String destination, LocalDateTime departureTime) {
        lineText.setText("Linia " + line);
        destinationText.setText(destination);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        departureTimeText.setText("Odjazd: " + departureTime.format(formatter));

        long minutes = Duration.between(LocalDateTime.now(), departureTime).toMinutes();
        String countdown = minutes >= 0 ? minutes + " min" : "Odjechał";
        countdownText.setText(countdown);
    }
}

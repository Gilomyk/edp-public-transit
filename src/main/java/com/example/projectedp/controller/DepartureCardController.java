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

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        String timeStr = departureTime.format(fmt);

        // Dodajemy „jutro”, jeśli godzina dnia następnego
        String label = "Odjazd: " + timeStr;
        if (departureTime.toLocalDate().isAfter(now.toLocalDate())) {
            label += " (jutro)";
        }
        departureTimeText.setText(label);

        long minutes = Duration.between(now, departureTime).toMinutes();
        countdownText.setText(minutes >= 0 ? minutes + " min" : "Odjechał");
    }

}

package com.example.projectedp_19.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainController {

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

    @FXML
    public void initialize() {
        // Przykładowe dane do testu
        stopList.getItems().addAll("Rondo Solidarności", "Dworzec", "Piłsudskiego");
        departureList.getItems().addAll("Linia 5 → Centrum", "Linia 8 → Dworzec");
    }
}
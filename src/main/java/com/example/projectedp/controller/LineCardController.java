package com.example.projectedp.controller;

import com.example.projectedp.model.Line;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LineCardController {
    @FXML
    private Label lineNumberLabel;

    @FXML
    private Label directionLabel;

    public void setData(Line line) {
        lineNumberLabel.setText(line.getLineNumber());

        // Jeśli lista kierunków jest pusta, nie pokazujemy nic
        if (line.getDirection().isEmpty()) {
            directionLabel.setText("");
        } else {
            directionLabel.setText(String.join(" / ", line.getDirection()));
        }
    }

}

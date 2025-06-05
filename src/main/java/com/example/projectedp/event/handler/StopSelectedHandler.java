package com.example.projectedp.event.handler;

import com.example.projectedp.event.StopSelectedEvent;

public class StopSelectedHandler {
    public void handle(StopSelectedEvent event) {
        System.out.println("Wybrano przystanek: " + event.getSelectedStop().getName());
        // Fetchuj najbliższe odjazdy itp.
    }
}

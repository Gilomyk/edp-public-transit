package com.example.projectedp.event.handler;

import com.example.projectedp.event.FavoriteSavedEvent;

public class FavoriteSavedHandler {
    public void handle(FavoriteSavedEvent event) {
        System.out.println("❤️ Zapisano ulubiony przystanek: " + event.getStop().getName());
        // Zapisz do bazy
    }
}
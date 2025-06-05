package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.StopAddedToFavoritesEvent;
import com.example.projectedp.model.Stop;

public class StopAddedToFavoritesHandler implements EventHandler<StopAddedToFavoritesEvent> {
    @Override
    public void handle(StopAddedToFavoritesEvent event) {
        Stop stop = event.getStop();
        System.out.println("Dodano do ulubionych: " + stop.getName() + " (ID: " + stop.getId() + ")");
    }
}


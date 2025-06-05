package com.example.projectedp.event.handler;

import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.StopAddedToFavoritesEvent;

public class StopAddedToFavoritesHandler implements EventHandler<StopAddedToFavoritesEvent> {
    @Override
    public void handle(StopAddedToFavoritesEvent event) {
        System.out.println("Dodano do ulubionych: " + event.getStopName());
    }
}

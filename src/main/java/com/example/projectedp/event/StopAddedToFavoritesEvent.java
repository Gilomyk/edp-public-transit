package com.example.projectedp.event;

public class StopAddedToFavoritesEvent {
    private final String stopName;

    public StopAddedToFavoritesEvent(String stopName) {
        this.stopName = stopName;
    }

    public String getStopName() {
        return stopName;
    }
}
package com.example.projectedp.event;

import com.example.projectedp.model.Stop;

public class StopAddedToFavoritesEvent {
    private final Stop stop;

    public StopAddedToFavoritesEvent(Stop stop) {
        this.stop = stop;
    }

    public Stop getStop() {
        return stop;
    }
}

package com.example.projectedp.event;

import com.example.projectedp.model.Stop;

public class StopRemovedFromFavoritesEvent {
    private final Stop stop;

    public StopRemovedFromFavoritesEvent(Stop stop) {
        this.stop = stop;
    }

    public Stop getStop() {
        return stop;
    }
}


package com.example.projectedp.event;

import com.example.projectedp.model.Stop;

public class FavoriteSavedEvent {
    private final Stop stop;

    public FavoriteSavedEvent(Stop stop) {
        this.stop = stop;
    }

    public Stop getStop() {
        return stop;
    }
}

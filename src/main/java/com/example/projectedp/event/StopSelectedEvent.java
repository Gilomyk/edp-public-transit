package com.example.projectedp.event;

import com.example.projectedp.model.Stop;

public class StopSelectedEvent {
    private final Stop selectedStop;

    public StopSelectedEvent(Stop selectedStop) {
        this.selectedStop = selectedStop;
    }

    public Stop getSelectedStop() {
        return selectedStop;
    }
}

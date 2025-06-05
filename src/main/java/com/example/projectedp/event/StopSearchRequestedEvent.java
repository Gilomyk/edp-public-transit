package com.example.projectedp.event;

public class StopSearchRequestedEvent {
    private final String stopName;

    public StopSearchRequestedEvent(String stopName) {
        this.stopName = stopName;
    }

    public String getStopName() {
        return stopName;
    }
}
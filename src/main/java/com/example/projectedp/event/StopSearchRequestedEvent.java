package com.example.projectedp.event;

public class StopSearchRequestedEvent {
    private final String query;

    public StopSearchRequestedEvent(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
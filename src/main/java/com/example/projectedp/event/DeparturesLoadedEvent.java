package com.example.projectedp.event;

import com.example.projectedp.model.Departure;

import java.util.List;

public class DeparturesLoadedEvent {
    private final List<Departure> departures;

    public DeparturesLoadedEvent(List<Departure> departures) {
        this.departures = departures;
    }

    public List<Departure> getDepartures() {
        return departures;
    }
}

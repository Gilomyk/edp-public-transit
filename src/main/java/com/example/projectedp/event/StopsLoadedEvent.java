package com.example.projectedp.event;

import com.example.projectedp.model.Stop;
import java.util.List;

/**
 * Zdarzenie sygnalizujące, że została załadowana (i sparsowana) lista przystanków.
 */
public class StopsLoadedEvent {
    private final List<Stop> stops;

    public StopsLoadedEvent(List<Stop> stops) {
        this.stops = stops;
    }

    public List<Stop> getStops() {
        return stops;
    }
}

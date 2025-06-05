package com.example.projectedp.event.handler;

import com.example.projectedp.event.StopSearchRequestedEvent;

public class StopSearchRequestedHandler {
    public void handle(StopSearchRequestedEvent event) {
        System.out.println("Wyszukiwanie przystanku: " + event.getStopName());
        // Wywołaj serwis API, pobierz listę przystanków itd.
    }
}

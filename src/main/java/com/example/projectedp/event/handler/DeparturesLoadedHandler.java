package com.example.projectedp.event.handler;


import com.example.projectedp.event.DeparturesLoadedEvent;

public class DeparturesLoadedHandler {
    public void handle(DeparturesLoadedEvent event) {
        System.out.println("Odebrano odjazdy: " + event.getDepartures().size() + " pozycji");
        // Odśwież widok
    }
}

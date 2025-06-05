package com.example.projectedp.model;

import java.time.LocalDateTime;

public class Departure {
    private final String id;
    private final String line;
    private final String destination;
    private final LocalDateTime time;

    public Departure(String id, String line, String destination, LocalDateTime time) {
        this.id = id;
        this.line = line;
        this.destination = destination;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getLine() {
        return line;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public String toString() {
        return line + " → " + destination + " o " + time.toLocalTime();
    }
}
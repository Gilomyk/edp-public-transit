package com.example.projectedp.model;

import java.time.LocalDateTime;

public class RecentSearch {
    private final String query;
    private final LocalDateTime timestamp;

    public RecentSearch(String query, LocalDateTime timestamp) {
        this.query = query;
        this.timestamp = timestamp;
    }

    public String getQuery() {
        return query;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return query + " (" + timestamp.toLocalTime() + ")";
    }
}


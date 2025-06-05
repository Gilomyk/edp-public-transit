package com.example.projectedp.event;

public class NotificationRequestedEvent {
    private final String departureId;

    public NotificationRequestedEvent(String departureId) {
        this.departureId = departureId;
    }

    public String getDepartureId() {
        return departureId;
    }
}

package com.example.projectedp.event;

public class NotificationRequestedEvent{
    private final String message;

    public NotificationRequestedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
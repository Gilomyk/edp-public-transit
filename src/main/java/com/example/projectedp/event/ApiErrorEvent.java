package com.example.projectedp.event;

public class ApiErrorEvent {
    private final String message;

    public ApiErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
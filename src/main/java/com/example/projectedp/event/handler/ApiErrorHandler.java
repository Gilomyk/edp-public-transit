package com.example.projectedp.event.handler;

import com.example.projectedp.event.ApiErrorEvent;

public class ApiErrorHandler {
    public void handle(ApiErrorEvent event) {
        System.err.println("❌ Błąd API: " + event.getMessage());
        // Wyświetl komunikat użytkownikowi
    }
}

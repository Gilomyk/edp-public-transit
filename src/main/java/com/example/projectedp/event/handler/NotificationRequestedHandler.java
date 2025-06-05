package com.example.projectedp.event.handler;

import com.example.projectedp.event.NotificationRequestedEvent;
import com.example.projectedp.event.EventHandler;
public class NotificationRequestedHandler implements EventHandler<NotificationRequestedEvent> {
    @Override
    public void handle(NotificationRequestedEvent event) {
        System.out.println("Powiadomienie: " + event.getMessage());
        // Tu można rozbudować np. o alert w GUI albo toast
    }
}

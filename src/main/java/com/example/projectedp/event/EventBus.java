package com.example.projectedp.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    private final Map<Class<?>, List<EventHandler<?>>> handlers = new HashMap<>();

    public <E> void register(Class<E> eventType, EventHandler<E> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }

    public <E> void post(E event) {
        List<EventHandler<?>> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler<?> handler : eventHandlers) {
                @SuppressWarnings("unchecked")
                EventHandler<E> typedHandler = (EventHandler<E>) handler;
                typedHandler.handle(event);
            }
        }
    }
}

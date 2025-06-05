package com.example.projectedp.event;

public interface EventHandler<E> {
    void handle(E event);
}

package com.example.projectedp.util;

import com.example.projectedp.event.EventBus;
import com.example.projectedp.event.EventHandler;
import com.example.projectedp.event.Handles;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

public class EventHandlerLoader {

    public static void loadHandlers(EventBus bus, String basePackage, Map<Class<?>, Object> dependencies) {
        Reflections reflections = new Reflections(basePackage);
        Set<Class<? extends EventHandler>> handlerClasses = reflections.getSubTypesOf(EventHandler.class);

        for (Class<? extends EventHandler> handlerClass : handlerClasses) {
            Handles annotation = handlerClass.getAnnotation(Handles.class);
            if (annotation == null) continue;

            try {
                EventHandler<?> handler = null;

                for (Constructor<?> constructor : handlerClass.getDeclaredConstructors()) {
                    Class<?>[] paramTypes = constructor.getParameterTypes();
                    Object[] args = new Object[paramTypes.length];
                    boolean canConstruct = true;

                    for (int i = 0; i < paramTypes.length; i++) {
                        Object dep = dependencies.get(paramTypes[i]);
                        if (dep == null) {
                            canConstruct = false;
                            break;
                        }
                        args[i] = dep;
                    }

                    if (canConstruct) {
                        constructor.setAccessible(true);
                        handler = (EventHandler<?>) constructor.newInstance(args);
                        break;
                    }
                }

                if (handler == null) {
                    System.err.println("Brak odpowiedniego konstruktora dla: " + handlerClass.getSimpleName());
                    continue;
                }

                @SuppressWarnings("unchecked")
                Class<Object> eventType = (Class<Object>) annotation.value();

                @SuppressWarnings("unchecked")
                EventHandler<Object> castedHandler = (EventHandler<Object>) handler;

                bus.register(eventType, castedHandler);
                System.out.println("Zarejestrowano handler: " + handlerClass.getSimpleName());

            } catch (Exception e) {
                System.err.println("Błąd przy ładowaniu handlera: " + handlerClass.getSimpleName());
                e.printStackTrace();
            }
        }
    }
}

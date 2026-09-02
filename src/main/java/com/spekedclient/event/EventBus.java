package com.spekedclient.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class EventBus {
    private final Map<Class<?>, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> type, Consumer<T> listener) {
        listeners.computeIfAbsent(type, ignored -> new ArrayList<>()).add(event -> listener.accept(type.cast(event)));
    }

    public <T> void unsubscribe(Class<T> type, Consumer<T> listener) {
        List<Consumer<Object>> entries = listeners.get(type);
        if (entries != null) entries.removeIf(entry -> entry.equals(listener));
    }

    public void post(Object event) {
        if (event == null) return;
        listeners.forEach((type, entries) -> {
            if (type.isInstance(event)) entries.forEach(listener -> listener.accept(event));
        });
    }

    public void clear() {
        listeners.clear();
    }
}

package hydrogen.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventBus {
    private final Map<Class<?>, List<Listener<?>>> listeners = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> eventType, Listener<T> listener) {
        listeners.computeIfAbsent(eventType, ignored -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public <T> void unsubscribe(Class<T> eventType, Listener<T> listener) {
        List<Listener<?>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
            if (list.isEmpty()) {
                listeners.remove(eventType, list);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void post(T event) {
        List<Listener<?>> list = listeners.get(event.getClass());
        if (list == null) return;
        for (Listener<?> listener : list) {
            ((Listener<T>) listener).invoke(event);
        }
    }

    public interface Listener<T> {
        void invoke(T event);
    }
}

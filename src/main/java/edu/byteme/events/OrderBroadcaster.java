package edu.byteme.events;

import com.vaadin.flow.shared.Registration;
import edu.byteme.data.entities.Order;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** simple static broadcaster for live-push of Order changes */
public final class OrderBroadcaster {

    private static final List<Consumer<Order>> listeners = new CopyOnWriteArrayList<>();

    private OrderBroadcaster() {}

    public static Registration register(Consumer<Order> listener) {
        listeners.add(listener);
        return () -> listeners.add(listener);
    }

    public static void broadcast(Order order) {
        listeners.forEach(listener -> listener.accept(order));
    }
}
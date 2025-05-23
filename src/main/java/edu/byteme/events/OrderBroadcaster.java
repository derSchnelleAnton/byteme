package edu.byteme.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import com.vaadin.flow.shared.Registration;

import edu.byteme.data.entities.Order;

/** simple static broadcaster for live-push of Order changes */
public final class OrderBroadcaster {

    private static final List<Consumer<Order>> listeners = new CopyOnWriteArrayList<>();

    private OrderBroadcaster() {}

    public static Registration register(Consumer<Order> listener) {
        listeners.add(listener);
        System.out.println("DEBUG: OrderBroadcaster - Registered new listener, total listeners: " + listeners.size());
        return () -> {
            boolean removed = listeners.remove(listener);
            System.out.println("DEBUG: OrderBroadcaster - Listener removed: " + removed + ", remaining listeners: " + listeners.size());
        };
    }

    public static void broadcast(Order order) {
        System.out.println("DEBUG: OrderBroadcaster - Broadcasting order #" + order.getId() +
                          " with status: " + order.getStatus() +
                          " to " + listeners.size() + " listeners");
        listeners.forEach(listener -> {
            try {
                listener.accept(order);
            } catch (Exception e) {
                System.err.println("DEBUG: OrderBroadcaster - Error notifying listener: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
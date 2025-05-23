package edu.byteme.views.orders;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.spring.annotation.UIScope;

import edu.byteme.data.entities.Order;
import edu.byteme.data.entities.OrderStatus;
import edu.byteme.events.OrderBroadcaster;

import java.util.logging.Logger;

/**
 * A component that displays the timeline for an order's status progression.
 * Handles real-time updates from the OrderBroadcaster.
 */
@UIScope
public class OrderTimeLine extends Div {
    private static final Logger LOGGER = Logger.getLogger(OrderTimeLine.class.getName());
    
    // The order being displayed
    private Order currentOrder;
    
    // UI Components
    private final ProgressBar progressBar;
    private final H2 statusText;
    private final Div progressContainer;
    
    // Broadcaster registration
    private Registration broadcasterRegistration;

    /**
     * Creates a new OrderTimeLine component for the given order.
     *
     * @param order The order to display
     */
    public OrderTimeLine(Order order) {
        this.currentOrder = order;
        
        // Set up UI components once during initialization
        this.progressBar = new ProgressBar();
        this.progressBar.setMin(0);
        this.progressBar.setMax(1);
        
        this.progressContainer = new Div();
        this.progressContainer.setId("progress-wrapper");
        this.setWidthFull();
        
        Div circle1 = new Div();
        circle1.addClassName("progress-marker");
        
        Div circle2 = new Div();
        circle2.addClassName("progress-marker");
        
        Div circle3 = new Div();
        circle3.addClassName("progress-marker");
        
        this.progressContainer.add(this.progressBar, circle1, circle2, circle3);
        
        this.statusText = new H2();
        this.statusText.getStyle()
                .set("text-align", "center")
                .set("width", "100%")
                .set("margin-bottom", "40px")
                .set("color", "#006AF5");
        
        // Add components to the layout
        add(this.progressContainer, this.statusText);
        
        // Initialize UI state based on current order
        updateUIState();
        
        // Register for updates from the broadcaster
        registerForOrderUpdates();
        
        LOGGER.info("OrderTimeLine created for order #" + order.getId() + " with status " + order.getStatus());
    }
    
    /**
     * Updates the UI components to reflect the current order state.
     */
    private void updateUIState() {
        OrderStatus status = currentOrder.getStatus();
        
        // Update progress bar value
        progressBar.setValue(mapStatusToProgress(status));
        
        // Update status text
        statusText.setText(mapStatusToMessage(status));
        
        LOGGER.info("Updated UI components for order #" + currentOrder.getId() + 
                   " with status " + status + 
                   ", progress: " + mapStatusToProgress(status) + 
                   ", message: '" + mapStatusToMessage(status) + "'");
    }
    
    /**
     * Maps an OrderStatus to a progress value between 0 and 1.
     *
     * @param status The order status
     * @return A value between 0 and 1 representing the progress
     */
    private double mapStatusToProgress(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> 0.15;
            case IN_PROGRESS -> 0.35;
            case IN_DELIVERY -> 0.75;
            case DELIVERED -> 1.0;
            default -> 0.0; // PENDING, CANCELLED, or unknown status
        };
    }
    
    /**
     * Maps an OrderStatus to a user-friendly message.
     *
     * @param status The order status
     * @return A user-friendly message describing the status
     */
    private String mapStatusToMessage(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "Your order has been confirmed";
            case IN_PROGRESS -> "Your order is being prepared";
            case IN_DELIVERY -> "We are on our way";
            case DELIVERED -> "Enjoy!";
            case CANCELLED -> "Your order has been cancelled";
            default -> "We received your order"; // PENDING or unknown status
        };
    }
    
    /**
     * Registers for updates from the OrderBroadcaster.
     */
    private void registerForOrderUpdates() {
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
        }
        
        broadcasterRegistration = OrderBroadcaster.register(this::handleOrderUpdate);
        LOGGER.info("Registered for order updates for order #" + currentOrder.getId());
    }
    
    /**
     * Handles updates received from the OrderBroadcaster.
     *
     * @param updatedOrder The updated order
     */
    private void handleOrderUpdate(Order updatedOrder) {
        // Only process updates for the order we're displaying
        if (!updatedOrder.getId().equals(currentOrder.getId())) {
            return;
        }
        
        LOGGER.info("Received update for order #" + updatedOrder.getId() + 
                   ": Status changed from " + currentOrder.getStatus() + 
                   " to " + updatedOrder.getStatus());
        
        // Update our reference to the order
        this.currentOrder = updatedOrder;
        
        // Update the UI on the UI thread
        UI ui = getUI().orElse(null);
        if (ui != null && ui.isAttached()) {
            ui.access(() -> {
                try {
                    updateUIState();
                    LOGGER.info("Successfully updated UI for order #" + updatedOrder.getId());
                } catch (Exception e) {
                    LOGGER.severe("Error updating UI: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            LOGGER.warning("UI not available, couldn't update for order #" + updatedOrder.getId());
        }
    }
    
    /**
     * Updates the order being displayed.
     *
     * @param order The new order to display
     */
    public void setOrder(Order order) {
        this.currentOrder = order;
        updateUIState();
        LOGGER.info("Order changed to #" + order.getId() + " with status " + order.getStatus());
    }
    
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        // Clean up the broadcaster registration to prevent memory leaks
        if (broadcasterRegistration != null) {
            broadcasterRegistration.remove();
            broadcasterRegistration = null;
            LOGGER.info("Unregistered from order updates for order #" + currentOrder.getId());
        }
    }
}

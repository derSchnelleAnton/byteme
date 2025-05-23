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

@UIScope
public class OrderTimeLine extends Div {
    private Order order;
    private Registration orderUpdateRegistration;

    public OrderTimeLine (Order order) {
        this.order = order;
        draw();

        // Register for order updates
        orderUpdateRegistration = OrderBroadcaster.register(this::handleOrderUpdate);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);
        // Clean up subscription when component is detached
        if (orderUpdateRegistration != null) {
            orderUpdateRegistration.remove();
            orderUpdateRegistration = null;
        }
    }

    /**
     * Handle order updates from the OrderBroadcaster
     */
    private void handleOrderUpdate(Order updatedOrder) {
        if (updatedOrder.getId().equals(order.getId())) {
            // Update our local order reference
            UI ui = UI.getCurrent();
            if (ui == null) return;
            
            ui.access(() -> {
                System.out.println("DEBUG: OrderTimeLine: Updating order #" + updatedOrder.getId() + 
                                  " status to " + updatedOrder.getStatus());
                
                // Store the updated order and redraw the component
                this.order = updatedOrder;
                removeAll();
                draw();
            });
        }
    }

    private void draw() {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMin(0);
        progressBar.setMax(1);
        progressBar.setValue(mapEnumToPercent(order.getStatus()));

        Div container = new Div();
        container.setId("progress-wrapper");
        setWidthFull();

        Div circle1 = new Div();
        circle1.addClassName("progress-marker");

        Div circle2 = new Div();
        circle2.addClassName("progress-marker");

        Div circle3 = new Div();
        circle3.addClassName("progress-marker");

        container.add(progressBar, circle1, circle2, circle3);

        H2 exclamationText = new H2(getStatusText(order.getStatus()));
        exclamationText.getStyle()
                .set("text-align", "center")
                .set("width", "100%")
                .set("margin-bottom", "40px")
                .set("color", "#006AF5");

        add(container, exclamationText);
    }

    private double mapEnumToPercent(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> 0.15;
            case IN_PROGRESS -> 0.35;
            case IN_DELIVERY -> 0.75;
            case DELIVERED -> 1.0;
            default -> 0.0;
        };
    }

    private String getStatusText(OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> "Your order has been confirmed";
            case IN_PROGRESS -> "Your order is being prepared";
            case IN_DELIVERY -> "We are on our way";
            case DELIVERED -> "Enjoy!";
            default -> "We received your order";
        };
    }

    public void setValues(Order order){
        this.order = order;
        removeAll();
        draw();
    }
}

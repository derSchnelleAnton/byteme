package edu.byteme.views.menu;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.spring.annotation.UIScope;
import edu.byteme.data.entities.Order;
import edu.byteme.data.entities.OrderStatus;

@UIScope
public class OrderTimeLine extends Div {
    private Order order;

    public OrderTimeLine (Order order) {
        this.order = order;
        draw();
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

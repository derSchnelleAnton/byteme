package edu.byteme.views.orders;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

import com.vaadin.flow.spring.annotation.UIScope;
import edu.byteme.data.entities.Order;
import edu.byteme.data.entities.OrderStatus;
import edu.byteme.services.OrderService;

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
        HorizontalLayout bottomBar = new HorizontalLayout();
        bottomBar.addClassName("total");
        bottomBar.add(new Paragraph("Total price "), new Paragraph(OrderService.getTotalCostOfOrder(order)+"$"));
        add(bottomBar, container);
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

    public void setValues(Order order){
        this.order = order;
        removeAll();
        draw();
    }
}

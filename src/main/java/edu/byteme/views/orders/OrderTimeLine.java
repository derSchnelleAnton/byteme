package edu.byteme.views.orders;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

import edu.byteme.data.entities.Order;
import edu.byteme.data.entities.OrderStatus;

public class OrderTimeLine extends Div {

    private Order order;
    String total;

    public OrderTimeLine(Order order, String total){
        this.order = order;
        this.total = total;
        draw();

    }



    private void draw(){
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
        bottomBar.add(new Paragraph("Total price "), new Paragraph(total));
        add(bottomBar,container);

    }


    private double mapEnumToPercent(OrderStatus status){

        switch (status) {
            case PENDING:
                return 0.25;
            case CONFIRMED:
                return 0.50;
            case IN_PROGRESS:
                return 0.75;
            case DELIVERED:
                return 1.0;
            default:
                return 0.0;
        }
    }


    public void setValues(Order order, String total){
        this.total = total;
        this.order = order;
        removeAll();
        draw();

    }
    
}

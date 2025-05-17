package edu.byteme.views.side_bar;


import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.avatar.Avatar;

import edu.byteme.data.entities.Order;
import edu.byteme.services.OrderService;

import java.time.format.DateTimeFormatter;
import java.util.List;




public class SideBar extends VerticalLayout{
    private List<Order> orders;
    private OnOrderSelectedListener onOrderSelectedListener;
    private OrderService orderService;


    public SideBar(OrderService orderService){

        this.orderService = orderService;
        this.orders = orderService.getOrdersByClientId(5);

        createNavItems();
    }

    private void createNavItems(){
        ListBox<Order> listBox = new ListBox<>();
        listBox.setItems(orders);
        listBox.setValue(orders.get(0));
        listBox.setRenderer(new ComponentRenderer<>(order -> {
            HorizontalLayout row = new HorizontalLayout();
            Avatar avatar = new Avatar("Lunch Box");
            avatar.setImage("images/food.png");
            row.add(avatar);
            String date = order.getOrderDate().format(DateTimeFormatter.ofPattern("dd. MM. yyyy"));
            row.add(new Span(date));
            row.add(new Span(orderService.getTotalCostOfOrder(order)+"€"));
            row.setDefaultVerticalComponentAlignment(Alignment.CENTER);
            return row;
        }));

        listBox.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                onOrderSelectedListener.orderSelected(e.getValue());
            }
        });

        add(listBox);
        setSizeFull();

    }
    

    public interface OnOrderSelectedListener{
        void orderSelected(Order order);
    }

    public void setOnOrderSelectedListener(OnOrderSelectedListener onOrderSelectedListener) {
        this.onOrderSelectedListener = onOrderSelectedListener;
    }
    
}

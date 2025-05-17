package edu.byteme.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import edu.byteme.data.entities.Order;
import edu.byteme.data.repositories.OrderRepository;

import edu.byteme.data.entities.OrderStatus;
import edu.byteme.events.OrderBroadcaster;
import edu.byteme.data.entities.MenuItem;
/*
* stuff for orderin
* validation of order
* status of order
* idk
* */


@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // CRUD and Query Methods

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(int id) {
        return orderRepository.findById(id);
    }

    public Order getReferenceById(int id) {
        return orderRepository.getReferenceById(id);
    }

    public List<Order> getOrdersByClientId(int clientId) {
        return orderRepository.findByClientId(clientId);
    }

    public List<Order> getOrdersByAdminId(int adminId) {
        return orderRepository.findByAdminId(adminId);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order saveAndBroadcast(Order order) {
        Order saved = orderRepository.save(order);
        OrderBroadcaster.broadcast(saved);
        return saved;
    }

    public void deleteOrder(int id) {
        orderRepository.deleteById(id);
    }

    public boolean existsById(int id) {
        return orderRepository.existsById(id);
    }


    public double getTotalCostOfOrder(Order order){
        double price = 0.0;
        for (MenuItem item: order.getMenuItems()) {
            price+= item.getPrice();
        }
        return price;
    }

    // Optional: update status
//    public Order updateStatus(int orderId, OrderStatus newStatus) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
//        order.setStatus(newStatus);
//        return orderRepository.save(order);
//    }
    public Order updateStatus(int orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        OrderBroadcaster.broadcast(saved);
        return saved;
    }

    public Order nextStage(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatus next = switch (order.getStatus()) {
            case PENDING      -> OrderStatus.CONFIRMED;
            case CONFIRMED    -> OrderStatus.IN_PROGRESS;
            case IN_PROGRESS  -> OrderStatus.DELIVERED;
            default           -> order.getStatus();   // DELIVERED or CANCELLED stay
        };

        order.setStatus(next);
        Order saved = orderRepository.save(order);
        OrderBroadcaster.broadcast(saved);
        return saved;
    }

    public void setStatus(Integer id, OrderStatus value) {
    }
}
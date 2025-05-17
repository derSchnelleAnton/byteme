package edu.byteme.data.repositories;

/*
 * spring data repository for Order
 */

import edu.byteme.data.entities.Order;
import edu.byteme.data.entities.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // get all orders for a specific client
    List<Order> findByClientId(Integer clientId);

    // get all orders assigned to a specific admin
    List<Order> findByAdminId(Integer adminId);

    // find by status of order
    List<Order> findByStatus(OrderStatus status);

    // find by order date
    List<Order> findByOrderDate(LocalDateTime orderDate);
}

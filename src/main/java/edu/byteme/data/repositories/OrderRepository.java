package edu.byteme.data.repositories;

/*
 * spring data repository for Order
 */

import edu.byteme.data.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    // get all orders for a specific client
    List<Order> findByClientId(Integer clientId);

    // get all orders assigned to a specific admin
    List<Order> findByAdminId(Integer adminId);
}

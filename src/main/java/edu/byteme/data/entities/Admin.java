package edu.byteme.data.entities;

import jakarta.persistence.*;
import edu.byteme.data.entities.User;

import java.util.ArrayList;
import java.util.List;

/*
 * handles order processing and status updates
 * tracks all orders assigned to this admin
 * can be used for reports and admin-only dashboard access
 */

@Entity
@Table(name = "users")
public class Admin extends User {

    private String role;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    // set admin role
    public void setRole(String role) {
        this.role = role;
    }

    // get admin role
    public String getRole() {
        return role;
    }

    // return all orders assigned to this admin
    public List<Order> getOrders() {
        return orders;
    }

    // return a single order by its index in the list
    public Order getOrder(int index) {
        if (index < 0 || index >= orders.size()) {
            return null;
        }
        return orders.get(index);
    }

    // add an order by index position
    public boolean addOrder(int index) {
        if (index < 0 || index > orders.size()) {
            return false;
        }
        orders.add(index, new Order()); // placeholder obj
        return true;
    }

    // remove an order by index
    public boolean removeOrder(int index) {
        if (index < 0 || index >= orders.size()) {
            return false;
        }
        orders.remove(index);
        return true;
    }

    // update an order (replace existing one with matching id)
    public boolean updateOrder(Order updatedOrder) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId().equals(updatedOrder.getId())) {
                orders.set(i, updatedOrder);
                return true;
            }
        }
        return false;
    }
}

package edu.byteme.data.entities;

/*
 * holds address info and order history
 * inherits all base user fields and timestamps
 */

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class Client extends User {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    // set address for the client
    public void setAddress(Address address) {
        this.address = address;
    }

    // get the client's address
    public Address getAddress() {
        return address;
    }

    // get all orders placed by this client
    public List<Order> getOrders() {
        return orders;
    }

    // add a new order to the client
    public void addOrder(Order order) {
        orders.add(order);
        order.setClient(this);
    }

    // remove an order
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setClient(null);
    }
}

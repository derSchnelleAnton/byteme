package edu.byteme.data.entities;

/*
 * linked to both client (who placed it) and admin (who processes it)
 * contains multiple menu items
 * stores order status, creation time, and delivery info
 */

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="orders") //order is a reserved keyword in sql
public class Order extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_menu",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    private List<MenuItem> menuItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;

    // set the assigned admin
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    // get the assigned admin
    public Admin getAdmin() {
        return admin;
    }

    // set the client who placed the order
    public void setClient(Client client) {
        this.client = client;
    }

    // get the client who placed the order
    public Client getClient() {
        return client;
    }

    // set the items in the order
    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    // get the ordered menu items
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    // add a menu item to the order
    public void addMenuItem(MenuItem item) {
        this.menuItems.add(item);
    }

    // remove a menu item from the order
    public void removeMenuItem(MenuItem item) {
        this.menuItems.remove(item);
    }

    // set current status (e.g., NEW, IN_PROGRESS, DELIVERED)
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    // get current status
    public OrderStatus getStatus() {
        return status;
    }

    // set order creation time
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    // get order creation time
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    // set estimated or actual delivery time
    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    // get delivery time
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }
}

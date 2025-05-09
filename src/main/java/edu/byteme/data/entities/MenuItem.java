package edu.byteme.data.entities;

/*
 * contains name, description, price, discount, and availability
 * linked to orders through many-to-many relation
 */

import jakarta.persistence.*;

@Entity
public class MenuItem extends AbstractEntity {

    private String name;
    private String description;

    private double price;
    private double discount;
    private boolean isAvailable;

    // set menu item name
    public void setName(String name) {
        this.name = name;
    }

    // get menu item name
    public String getName() {
        return name;
    }

    // set item description
    public void setDescription(String description) {
        this.description = description;
    }

    // get item description
    public String getDescription() {
        return description;
    }

    // set price
    public void setPrice(double price) {
        this.price = price;
    }

    // get price
    public double getPrice() {
        return price;
    }

    // set discount
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // get discount
    public double getDiscount() {
        return discount;
    }

    // check if item is available
    public boolean isAvailable() {
        return isAvailable;
    }

    // set availability
    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}

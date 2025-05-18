package edu.byteme.data.entities;

/*
 * 1-1 relationship between it and Client based on Tinsae's schema
 */

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Address extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String postalCode;
    private String street;
    private int houseNumber;
    private String phone;

    // set postal code
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    // get postal code
    public String getPostalCode() {
        return postalCode;
    }

    // set street name
    public void setStreet(String street) {
        this.street = street;
    }

    // get street name
    public String getStreet() {
        return street;
    }

    // set house number
    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    // get house number
    public int getHouseNumber() {
        return houseNumber;
    }

    // set contact phone number
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // get contact phone number
    public String getPhone() {
        return phone;
    }
}

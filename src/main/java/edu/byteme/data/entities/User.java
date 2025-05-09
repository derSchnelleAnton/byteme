package edu.byteme.data.entities;

/*
 * holds shared fields like username, email, password, names
 * extends AbstractEntity for id + createdAt + updatedAt
 * used for login, profile info, etc.
 *
 *
 */

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass // not its own table, just inherited
public abstract class User extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    // getters
    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // settrs
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

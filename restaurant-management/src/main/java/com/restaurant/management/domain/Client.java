package com.restaurant.management.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENT")
public class Client extends User {

    protected Client() {
    }

    public Client(String name, String email, String login, String passwordHash, Address address) {
        super(name, email, login, passwordHash, address);
    }
}

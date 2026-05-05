package com.restaurant.management.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RESTAURANT_OWNER")
public class RestaurantOwner extends User {

    protected RestaurantOwner() {
    }

    public RestaurantOwner(String name, String email, String login, String passwordHash, Address address) {
        super(name, email, login, passwordHash, address);
    }
}

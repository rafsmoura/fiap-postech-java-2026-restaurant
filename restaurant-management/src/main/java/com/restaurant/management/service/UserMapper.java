package com.restaurant.management.service;

import com.restaurant.management.domain.Address;
import com.restaurant.management.domain.Client;
import com.restaurant.management.domain.RestaurantOwner;
import com.restaurant.management.domain.User;
import com.restaurant.management.dto.AddressRequest;
import com.restaurant.management.dto.AddressResponse;
import com.restaurant.management.dto.UserCreateRequest;
import com.restaurant.management.dto.UserResponse;
import com.restaurant.management.dto.UserType;

public final class UserMapper {

    private UserMapper() {
    }

    public static User createEntity(UserCreateRequest request, String passwordHash) {
        Address address = toAddress(request.address());
        return switch (request.userType()) {
            case RESTAURANT_OWNER -> new RestaurantOwner(
                    request.name(),
                    request.email().trim(),
                    request.login().trim(),
                    passwordHash,
                    address
            );
            case CLIENT -> new Client(
                    request.name(),
                    request.email().trim(),
                    request.login().trim(),
                    passwordHash,
                    address
            );
        };
    }

    public static void applyUpdate(User user, com.restaurant.management.dto.UserUpdateRequest request) {
        user.setName(request.name());
        user.setEmail(request.email().trim());
        user.setLogin(request.login().trim());
        user.setAddress(toAddress(request.address()));
        user.touchModified();
    }

    public static Address toAddress(AddressRequest a) {
        return new Address(
                a.street().trim(),
                a.number().trim(),
                a.city().trim(),
                a.zipCode().trim()
        );
    }

    public static UserResponse toResponse(User user) {
        Address a = user.getAddress();
        AddressResponse ar = new AddressResponse(
                a.getStreet(),
                a.getNumber(),
                a.getCity(),
                a.getZipCode()
        );
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                ar,
                userType(user),
                user.getLastModifiedAt()
        );
    }

    public static UserType userType(User user) {
        if (user instanceof RestaurantOwner) {
            return UserType.RESTAURANT_OWNER;
        }
        if (user instanceof Client) {
            return UserType.CLIENT;
        }
        throw new IllegalStateException("Tipo de usuário desconhecido: " + user.getClass());
    }
}

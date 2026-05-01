package com.restaurant.management.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("Usuário não encontrado com id: " + id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}

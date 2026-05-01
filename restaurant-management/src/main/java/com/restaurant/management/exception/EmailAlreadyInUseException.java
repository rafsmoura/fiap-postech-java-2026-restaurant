package com.restaurant.management.exception;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(String email) {
        super("E-mail já cadastrado: " + email);
    }
}

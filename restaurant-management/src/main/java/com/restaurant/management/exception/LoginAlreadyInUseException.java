package com.restaurant.management.exception;

public class LoginAlreadyInUseException extends RuntimeException {

    public LoginAlreadyInUseException(String login) {
        super("Login já cadastrado: " + login);
    }
}

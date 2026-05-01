package com.restaurant.management.exception;

public class InvalidCurrentPasswordException extends RuntimeException {

    public InvalidCurrentPasswordException() {
        super("Senha atual incorreta");
    }
}

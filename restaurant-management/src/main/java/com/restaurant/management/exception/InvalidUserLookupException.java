package com.restaurant.management.exception;

public class InvalidUserLookupException extends RuntimeException {

    public InvalidUserLookupException() {
        super("Informe exatamente um dos parâmetros: id, email, name (nome exato) ou nameContains (trecho do nome).");
    }

    public InvalidUserLookupException(String message) {
        super(message);
    }
}

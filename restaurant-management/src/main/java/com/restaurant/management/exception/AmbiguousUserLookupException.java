package com.restaurant.management.exception;

public class AmbiguousUserLookupException extends RuntimeException {

    public AmbiguousUserLookupException(int count) {
        super("Existem " + count + " usuários com o nome exato informado; use id ou e-mail para identificar o registro.");
    }

    public AmbiguousUserLookupException(String message) {
        super(message);
    }
}

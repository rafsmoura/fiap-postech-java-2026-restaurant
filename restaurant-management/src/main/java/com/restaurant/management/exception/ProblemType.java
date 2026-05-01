package com.restaurant.management.exception;

import java.net.URI;

public final class ProblemType {

    public static final URI BASE = URI.create("https://techchallenge.restaurant/problems");

    public static final URI USER_NOT_FOUND = BASE.resolve("user-not-found");
    public static final URI EMAIL_DUPLICATE = BASE.resolve("email-duplicate");
    public static final URI LOGIN_DUPLICATE = BASE.resolve("login-duplicate");
    public static final URI INVALID_CREDENTIALS = BASE.resolve("invalid-credentials");
    public static final URI INVALID_CURRENT_PASSWORD = BASE.resolve("invalid-current-password");
    public static final URI VALIDATION_ERROR = BASE.resolve("validation-error");
    public static final URI INVALID_USER_LOOKUP = BASE.resolve("invalid-user-lookup");
    public static final URI AMBIGUOUS_USER_NAME = BASE.resolve("ambiguous-user-name");

    private ProblemType() {
    }
}

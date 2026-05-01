package com.restaurant.management.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleNotFound(
            UserNotFoundException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Recurso não encontrado");
        pd.setType(ProblemType.USER_NOT_FOUND);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(InvalidUserLookupException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleInvalidUserLookup(
            InvalidUserLookupException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Parâmetro de busca inválido");
        pd.setType(ProblemType.INVALID_USER_LOOKUP);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(AmbiguousUserLookupException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleAmbiguousUserName(
            AmbiguousUserLookupException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Nome ambíguo");
        pd.setType(ProblemType.AMBIGUOUS_USER_NAME);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleEmailDuplicate(
            EmailAlreadyInUseException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Conflito de e-mail");
        pd.setType(ProblemType.EMAIL_DUPLICATE);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(LoginAlreadyInUseException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleLoginDuplicate(
            LoginAlreadyInUseException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Conflito de login");
        pd.setType(ProblemType.LOGIN_DUPLICATE);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleInvalidCredentials(
            InvalidCredentialsException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        pd.setTitle("Credenciais inválidas");
        pd.setType(ProblemType.INVALID_CREDENTIALS);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd);
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleInvalidCurrentPassword(
            InvalidCurrentPasswordException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Senha atual inválida");
        pd.setType(ProblemType.INVALID_CURRENT_PASSWORD);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::fieldErrorToMap)
                .collect(Collectors.toList());
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST, "Um ou mais campos são inválidos");
        pd.setTitle("Erro de validação");
        pd.setType(ProblemType.VALIDATION_ERROR);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleConstraint(
            ConstraintViolationException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Erro de validação");
        pd.setType(ProblemType.VALIDATION_ERROR);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST, "Corpo da requisição inválido ou malformado");
        pd.setTitle("Requisição inválida");
        pd.setType(ProblemType.VALIDATION_ERROR);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<org.springframework.http.ProblemDetail> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        org.springframework.http.ProblemDetail pd =
                org.springframework.http.ProblemDetail.forStatusAndDetail(
                        HttpStatus.BAD_REQUEST, "Parâmetro inválido: " + ex.getName());
        pd.setTitle("Requisição inválida");
        pd.setType(ProblemType.VALIDATION_ERROR);
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }

    private Map<String, String> fieldErrorToMap(FieldError fe) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("field", fe.getField());
        m.put("message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "inválido");
        return m;
    }
}

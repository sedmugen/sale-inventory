package io.saadmughal.saleinventorybackend.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long id) {
        super("Customer not found with ID: " + id);
    }

    public CustomerNotFoundException(String email) {
        super("Customer not found with email: " + email);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
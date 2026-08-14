package io.saadmughal.saleinventorybackend.exception;

public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(Long id) {
        super("Purchase not found with ID: " + id);
    }

    public PurchaseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

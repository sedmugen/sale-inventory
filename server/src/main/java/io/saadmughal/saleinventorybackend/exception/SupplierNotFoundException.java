package io.saadmughal.saleinventorybackend.exception;

public class SupplierNotFoundException extends RuntimeException {

    public SupplierNotFoundException(Long id) {
        super("Supplier not found with ID: " + id);
    }

    public SupplierNotFoundException(String email) {
        super("Supplier not found with email: " + email);
    }

    public SupplierNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
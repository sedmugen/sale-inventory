package io.saadmughal.saleinventorybackend.exception;

public class SaleNotFoundException extends RuntimeException {

    public SaleNotFoundException(Long id) {
        super("Sale not found with ID: " + id);
    }

    public SaleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

package io.saadmughal.saleinventorybackend.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product not found with ID: " + id);
    }

    public ProductNotFoundException(String code) {
        super("Product not found with code: " + code);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
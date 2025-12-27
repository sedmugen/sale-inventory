package io.saadmughal.saleinventorybackend.exception;

/**
 * Exception thrown when attempting to create a resource that already exists
 * Examples: duplicate email, duplicate product code
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    // Static factory methods for common scenarios

    public static DuplicateResourceException productCode(String code) {
        return new DuplicateResourceException(
                String.format("Product with code '%s' already exists", code)
        );
    }

    public static DuplicateResourceException customerEmail(String email) {
        return new DuplicateResourceException(
                String.format("Customer with email '%s' already exists", email)
        );
    }

    public static DuplicateResourceException supplierEmail(String email) {
        return new DuplicateResourceException(
                String.format("Supplier with email '%s' already exists", email)
        );
    }
}
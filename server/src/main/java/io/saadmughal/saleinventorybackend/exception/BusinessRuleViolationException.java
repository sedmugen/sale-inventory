package io.saadmughal.saleinventorybackend.exception;

/**
 * Exception thrown when business rules are violated
 * Examples: insufficient stock, blocked customer, inactive supplier
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }

    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    // Static factory methods for common scenarios

    public static BusinessRuleViolationException insufficientStock(Integer available, Integer requested) {
        return new BusinessRuleViolationException(
                String.format("Insufficient stock. Available: %d, Requested: %d", available, requested)
        );
    }

    public static BusinessRuleViolationException blockedCustomer(Long customerId) {
        return new BusinessRuleViolationException(
                String.format("Customer (ID: %d) is blocked. Cannot create sale.", customerId)
        );
    }

    public static BusinessRuleViolationException inactiveSupplier(Long supplierId) {
        return new BusinessRuleViolationException(
                String.format("Supplier (ID: %d) is inactive. Cannot create purchase.", supplierId)
        );
    }

    public static BusinessRuleViolationException inactiveProduct(Long productId) {
        return new BusinessRuleViolationException(
                String.format("Product (ID: %d) is inactive. Cannot create sale.", productId)
        );
    }

    public static BusinessRuleViolationException outOfStock(Long productId) {
        return new BusinessRuleViolationException(
                String.format("Product (ID: %d) is out of stock. Cannot create sale.", productId)
        );
    }
}
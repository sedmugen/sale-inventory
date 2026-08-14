package io.saadmughal.saleinventorybackend.repository;


import io.saadmughal.saleinventorybackend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by unique email
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Find all non-blocked customers
     */
    List<Customer> findByBlockedFalse();

    /**
     * Find all blocked customers
     */
    List<Customer> findByBlockedTrue();

    /**
     * Find customers by name containing (case-insensitive search)
     */
    List<Customer> findByNameContainingIgnoreCase(String name);

    /**
     * Find customers by email containing (case-insensitive search)
     */
    List<Customer> findByEmailContainingIgnoreCase(String email);

    /**
     * Check if customer with given email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if customer with given email exists (excluding specific ID)
     * Useful for update operations to check email uniqueness
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
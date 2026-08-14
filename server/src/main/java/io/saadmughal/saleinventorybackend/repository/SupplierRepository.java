package io.saadmughal.saleinventorybackend.repository;

import io.saadmughal.saleinventorybackend.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Find supplier by unique email
     */
    Optional<Supplier> findByEmail(String email);

    /**
     * Find all active suppliers
     */
    List<Supplier> findByActiveTrue();

    /**
     * Find all inactive suppliers
     */
    List<Supplier> findByActiveFalse();

    /**
     * Find suppliers by name containing (case-insensitive search)
     */
    List<Supplier> findByNameContainingIgnoreCase(String name);

    /**
     * Find suppliers by company name containing (case-insensitive search)
     */
    List<Supplier> findByCompanyNameContainingIgnoreCase(String companyName);

    /**
     * Find suppliers by email containing (case-insensitive search)
     */
    List<Supplier> findByEmailContainingIgnoreCase(String email);

    /**
     * Check if supplier with given email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if supplier with given email exists (excluding specific ID)
     * Useful for update operations to check email uniqueness
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}
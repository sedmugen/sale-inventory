package io.saadmughal.saleinventorybackend.repository;

import io.saadmughal.saleinventorybackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by unique code
     */
    Optional<Product> findByCode(String code);

    /**
     * Find all active products
     */
    List<Product> findByActiveTrue();

    /**
     * Find all inactive products
     */
    List<Product> findByActiveFalse();

    /**
     * Find products with stock less than specified limit
     */
    List<Product> findByCurrentStockLessThan(Integer stockLimit);

    /**
     * Find products by name containing (case-insensitive search)
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find products by code containing (case-insensitive search)
     */
    List<Product> findByCodeContainingIgnoreCase(String code);

    /**
     * Check if product with given code exists
     */
    boolean existsByCode(String code);

    /**
     * Find low stock products (stock < minStockLevel) with limit
     * Uses JOIN with ProductDetail to compare currentStock with minStockLevel
     */
    @Query("""
        SELECT p FROM Product p 
        JOIN ProductDetail pd ON pd.product.id = p.id 
        WHERE p.currentStock < pd.minStockLevel 
        AND p.active = true 
        ORDER BY p.currentStock ASC
        """)
    List<Product> findLowStockProducts();

    /**
     * Find active products with stock greater than or equal to quantity
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.currentStock >= :quantity")
    List<Product> findAvailableProductsWithStock(@Param("quantity") Integer quantity);
}
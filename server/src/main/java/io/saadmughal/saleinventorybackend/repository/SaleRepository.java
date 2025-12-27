package io.saadmughal.saleinventorybackend.repository;

import io.saadmughal.saleinventorybackend.entity.Sale;
import io.saadmughal.saleinventorybackend.entity.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Find all sales for a specific customer
     */
    List<Sale> findByCustomerId(Long customerId);

    /**
     * Find all sales for a specific product
     */
    List<Sale> findByProductId(Long productId);

    /**
     * Find sales by customer ID ordered by date descending
     */
    List<Sale> findByCustomerIdOrderByDateDesc(Long customerId);

    /**
     * Find sales by product ID ordered by date descending
     */
    List<Sale> findByProductIdOrderByDateDesc(Long productId);

    /**
     * Find sales by status
     */
    List<Sale> findByStatus(SaleStatus status);

    /**
     * Find sales by customer ID and status
     */
    List<Sale> findByCustomerIdAndStatus(Long customerId, SaleStatus status);

    /**
     * Find sales by product ID and status
     */
    List<Sale> findByProductIdAndStatus(Long productId, SaleStatus status);

    /**
     * Find sales within date range
     */
    List<Sale> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find sales by customer within date range
     */
    List<Sale> findByCustomerIdAndDateBetween(Long customerId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all sales ordered by date descending
     */
    List<Sale> findAllByOrderByDateDesc();

    /**
     * Find sales with product and customer details (optimized with JOIN FETCH)
     * Avoids N+1 query problem
     */
    @Query("""
        SELECT s FROM Sale s 
        JOIN FETCH s.product 
        JOIN FETCH s.customer 
        ORDER BY s.date DESC
        """)
    List<Sale> findAllWithDetails();

    /**
     * Find sales by customer ID with product details (optimized with JOIN FETCH)
     */
    @Query("""
        SELECT s FROM Sale s 
        JOIN FETCH s.product 
        JOIN FETCH s.customer c 
        WHERE c.id = :customerId 
        ORDER BY s.date DESC
        """)
    List<Sale> findByCustomerIdWithDetails(@Param("customerId") Long customerId);

    /**
     * Find sales by product ID with customer details (optimized with JOIN FETCH)
     */
    @Query("""
        SELECT s FROM Sale s 
        JOIN FETCH s.product p 
        JOIN FETCH s.customer 
        WHERE p.id = :productId 
        ORDER BY s.date DESC
        """)
    List<Sale> findByProductIdWithDetails(@Param("productId") Long productId);
}
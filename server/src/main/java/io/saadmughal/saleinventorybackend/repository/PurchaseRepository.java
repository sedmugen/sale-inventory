package io.saadmughal.saleinventorybackend.repository;

import io.saadmughal.saleinventorybackend.entity.Purchase;
import io.saadmughal.saleinventorybackend.entity.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /**
     * Find all purchases from a specific supplier
     */
    List<Purchase> findBySupplierId(Long supplierId);

    /**
     * Find all purchases for a specific product
     */
    List<Purchase> findByProductId(Long productId);

    /**
     * Find purchases by supplier ID ordered by date descending
     */
    List<Purchase> findBySupplierIdOrderByDateDesc(Long supplierId);

    /**
     * Find purchases by product ID ordered by date descending
     */
    List<Purchase> findByProductIdOrderByDateDesc(Long productId);

    /**
     * Find purchases by status
     */
    List<Purchase> findByStatus(PurchaseStatus status);

    /**
     * Find purchases by supplier ID and status
     */
    List<Purchase> findBySupplierIdAndStatus(Long supplierId, PurchaseStatus status);

    /**
     * Find purchases by product ID and status
     */
    List<Purchase> findByProductIdAndStatus(Long productId, PurchaseStatus status);

    /**
     * Find purchases within date range
     */
    List<Purchase> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find purchases by supplier within date range
     */
    List<Purchase> findBySupplierIdAndDateBetween(Long supplierId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all purchases ordered by date descending
     */
    List<Purchase> findAllByOrderByDateDesc();

    /**
     * Find purchases with product and supplier details (optimized with JOIN FETCH)
     * Avoids N+1 query problem
     */
    @Query("""
        SELECT p FROM Purchase p 
        JOIN FETCH p.product 
        JOIN FETCH p.supplier 
        ORDER BY p.date DESC
        """)
    List<Purchase> findAllWithDetails();

    /**
     * Find purchases by supplier ID with product details (optimized with JOIN FETCH)
     */
    @Query("""
        SELECT p FROM Purchase p 
        JOIN FETCH p.product 
        JOIN FETCH p.supplier s 
        WHERE s.id = :supplierId 
        ORDER BY p.date DESC
        """)
    List<Purchase> findBySupplierIdWithDetails(@Param("supplierId") Long supplierId);

    /**
     * Find purchases by product ID with supplier details (optimized with JOIN FETCH)
     */
    @Query("""
        SELECT p FROM Purchase p 
        JOIN FETCH p.product prod 
        JOIN FETCH p.supplier 
        WHERE prod.id = :productId 
        ORDER BY p.date DESC
        """)
    List<Purchase> findByProductIdWithDetails(@Param("productId") Long productId);
}
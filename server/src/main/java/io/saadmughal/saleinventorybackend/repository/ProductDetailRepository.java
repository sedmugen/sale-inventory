package io.saadmughal.saleinventorybackend.repository;

import io.saadmughal.saleinventorybackend.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {

    /**
     * Find product detail by product ID
     */
    Optional<ProductDetail> findByProductId(Long productId);

    /**
     * Find product details by category
     */
    List<ProductDetail> findByCategory(String category);

    /**
     * Find product details by brand
     */
    List<ProductDetail> findByBrand(String brand);

    /**
     * Find product details by category containing (case-insensitive)
     */
    List<ProductDetail> findByCategoryContainingIgnoreCase(String category);

    /**
     * Find product details by brand containing (case-insensitive)
     */
    List<ProductDetail> findByBrandContainingIgnoreCase(String brand);

    /**
     * Check if product detail exists for given product ID
     */
    boolean existsByProductId(Long productId);

    /**
     * Delete product detail by product ID
     */
    void deleteByProductId(Long productId);
}
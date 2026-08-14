package io.saadmughal.saleinventorybackend.service;

import io.saadmughal.saleinventorybackend.dto.request.PurchaseRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.PurchaseResponseDTO;
import io.saadmughal.saleinventorybackend.entity.Product;
import io.saadmughal.saleinventorybackend.entity.Purchase;
import io.saadmughal.saleinventorybackend.entity.PurchaseStatus;
import io.saadmughal.saleinventorybackend.entity.Supplier;
import io.saadmughal.saleinventorybackend.exception.BusinessRuleViolationException;
import io.saadmughal.saleinventorybackend.exception.ProductNotFoundException;
import io.saadmughal.saleinventorybackend.exception.SupplierNotFoundException;
import io.saadmughal.saleinventorybackend.repository.ProductRepository;
import io.saadmughal.saleinventorybackend.repository.PurchaseRepository;
import io.saadmughal.saleinventorybackend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    /**
     * Get all purchases
     */
    @Transactional(readOnly = true)
    public List<PurchaseResponseDTO> getAllPurchases() {
        return purchaseRepository.findAllWithDetails().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get purchase by ID
     */
    @Transactional(readOnly = true)
    public PurchaseResponseDTO getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found with ID: " + id));
        return convertToResponseDTO(purchase);
    }

    /**
     * Get purchases by supplier ID
     */
    @Transactional(readOnly = true)
    public List<PurchaseResponseDTO> getPurchasesBySupplierId(Long supplierId) {
        // Verify supplier exists
        if (!supplierRepository.existsById(supplierId)) {
            throw new SupplierNotFoundException(supplierId);
        }

        return purchaseRepository.findBySupplierIdWithDetails(supplierId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get purchases by product ID
     */
    @Transactional(readOnly = true)
    public List<PurchaseResponseDTO> getPurchasesByProductId(Long productId) {
        // Verify product exists
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }

        return purchaseRepository.findByProductIdWithDetails(productId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new purchase with business rule validation
     *
     * Business Rules:
     * 1. Quantity must be > 0
     * 2. Product must exist
     * 3. Supplier must exist
     * 4. Supplier must be active
     * 5. Unit cost must be provided and >= 0
     */
    @Transactional
    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO requestDTO) {
        // 1. Load and validate product
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(requestDTO.getProductId()));

        // 2. Load and validate supplier
        Supplier supplier = supplierRepository.findById(requestDTO.getSupplierId())
                .orElseThrow(() -> new SupplierNotFoundException(requestDTO.getSupplierId()));

        // 3. Check supplier is active
        if (!supplier.getActive()) {
            throw BusinessRuleViolationException.inactiveSupplier(supplier.getId());
        }

        // 4. Calculate total cost
        BigDecimal totalCost = requestDTO.getUnitCost()
                .multiply(BigDecimal.valueOf(requestDTO.getQuantity()));

        // 5. Create purchase entity
        Purchase purchase = Purchase.builder()
                .product(product)
                .supplier(supplier)
                .date(LocalDateTime.now())
                .quantity(requestDTO.getQuantity())
                .unitCost(requestDTO.getUnitCost())
                .totalCost(totalCost)
                .status(PurchaseStatus.RECEIVED)
                .build();

        // 6. Increase product stock
        product.setCurrentStock(product.getCurrentStock() + requestDTO.getQuantity());
        productRepository.save(product);

        // 7. Save purchase
        Purchase savedPurchase = purchaseRepository.save(purchase);

        return convertToResponseDTO(savedPurchase);
    }

    /**
     * Get purchases by status
     */
    @Transactional(readOnly = true)
    public List<PurchaseResponseDTO> getPurchasesByStatus(PurchaseStatus status) {
        return purchaseRepository.findByStatus(status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get purchases by supplier and status
     */
    @Transactional(readOnly = true)
    public List<PurchaseResponseDTO> getPurchasesBySupplierIdAndStatus(Long supplierId, PurchaseStatus status) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new SupplierNotFoundException(supplierId);
        }

        return purchaseRepository.findBySupplierIdAndStatus(supplierId, status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== DTO Conversion Methods ==========

    private PurchaseResponseDTO convertToResponseDTO(Purchase purchase) {
        // Build product info
        PurchaseResponseDTO.ProductInfo productInfo = PurchaseResponseDTO.ProductInfo.builder()
                .id(purchase.getProduct().getId())
                .code(purchase.getProduct().getCode())
                .name(purchase.getProduct().getName())
                .currentStock(purchase.getProduct().getCurrentStock())
                .build();

        // Build supplier info
        PurchaseResponseDTO.SupplierInfo supplierInfo = PurchaseResponseDTO.SupplierInfo.builder()
                .id(purchase.getSupplier().getId())
                .name(purchase.getSupplier().getName())
                .companyName(purchase.getSupplier().getCompanyName())
                .build();

        // Build purchase response
        return PurchaseResponseDTO.builder()
                .id(purchase.getId())
                .date(purchase.getDate())
                .quantity(purchase.getQuantity())
                .unitCost(purchase.getUnitCost())
                .totalCost(purchase.getTotalCost())
                .status(purchase.getStatus())
                .product(productInfo)
                .supplier(supplierInfo)
                .build();
    }
}
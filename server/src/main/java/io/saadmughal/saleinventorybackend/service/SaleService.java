package io.saadmughal.saleinventorybackend.service;

import io.saadmughal.saleinventorybackend.dto.request.SaleRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.SaleResponseDTO;
import io.saadmughal.saleinventorybackend.entity.Customer;
import io.saadmughal.saleinventorybackend.entity.Product;
import io.saadmughal.saleinventorybackend.entity.Sale;
import io.saadmughal.saleinventorybackend.entity.SaleStatus;
import io.saadmughal.saleinventorybackend.exception.BusinessRuleViolationException;
import io.saadmughal.saleinventorybackend.exception.CustomerNotFoundException;
import io.saadmughal.saleinventorybackend.exception.ProductNotFoundException;
import io.saadmughal.saleinventorybackend.exception.SaleNotFoundException;
import io.saadmughal.saleinventorybackend.repository.CustomerRepository;
import io.saadmughal.saleinventorybackend.repository.ProductRepository;
import io.saadmughal.saleinventorybackend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    /**
     * Get all sales
     */
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getAllSales() {
        return saleRepository.findAllWithDetails().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get sale by ID
     */
    @Transactional(readOnly = true)
    public SaleResponseDTO getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new SaleNotFoundException(id));
        return convertToResponseDTO(sale);
    }

    /**
     * Get sales by customer ID
     */
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSalesByCustomerId(Long customerId) {
        // Verify customer exists
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        return saleRepository.findByCustomerIdWithDetails(customerId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get sales by product ID
     */
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSalesByProductId(Long productId) {
        // Verify product exists
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }

        return saleRepository.findByProductIdWithDetails(productId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create new sale with business rule validation
     *
     * Business Rules:
     * 1. Quantity must be > 0
     * 2. Product must exist
     * 3. Product must be active
     * 4. Product must have sufficient stock
     * 5. Customer must exist
     * 6. Customer must not be blocked
     */
    @Transactional
    public SaleResponseDTO createSale(SaleRequestDTO requestDTO) {
        // 1. Load and validate product
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(requestDTO.getProductId()));

        // 2. Check product is active
        if (!product.getActive()) {
            throw BusinessRuleViolationException.inactiveProduct(product.getId());
        }

        // 3. Check product has stock
        if (product.getCurrentStock() == 0) {
            throw BusinessRuleViolationException.outOfStock(product.getId());
        }

        // 4. Check sufficient stock
        if (product.getCurrentStock() < requestDTO.getQuantity()) {
            throw BusinessRuleViolationException.insufficientStock(
                    product.getCurrentStock(),
                    requestDTO.getQuantity()
            );
        }

        // 5. Load and validate customer
        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(requestDTO.getCustomerId()));

        // 6. Check customer is not blocked
        if (customer.getBlocked()) {
            throw BusinessRuleViolationException.blockedCustomer(customer.getId());
        }

        // 7. Determine unit price (use provided or default to product price)
        BigDecimal unitPrice = requestDTO.getUnitPrice() != null
                ? requestDTO.getUnitPrice()
                : product.getUnitPrice();

        // 8. Calculate total price
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(requestDTO.getQuantity()));

        // 9. Create sale entity
        Sale sale = Sale.builder()
                .product(product)
                .customer(customer)
                .date(LocalDateTime.now())
                .quantity(requestDTO.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .status(SaleStatus.CONFIRMED)
                .build();

        // 10. Decrease product stock
        product.setCurrentStock(product.getCurrentStock() - requestDTO.getQuantity());
        productRepository.save(product);

        // 11. Save sale
        Sale savedSale = saleRepository.save(sale);

        return convertToResponseDTO(savedSale);
    }

    /**
     * Get sales by status
     */
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSalesByStatus(SaleStatus status) {
        return saleRepository.findByStatus(status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get sales by customer and status
     */
    @Transactional(readOnly = true)
    public List<SaleResponseDTO> getSalesByCustomerIdAndStatus(Long customerId, SaleStatus status) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        return saleRepository.findByCustomerIdAndStatus(customerId, status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // ========== DTO Conversion Methods ==========

    private SaleResponseDTO convertToResponseDTO(Sale sale) {
        // Build product info
        SaleResponseDTO.ProductInfo productInfo = SaleResponseDTO.ProductInfo.builder()
                .id(sale.getProduct().getId())
                .code(sale.getProduct().getCode())
                .name(sale.getProduct().getName())
                .currentStock(sale.getProduct().getCurrentStock())
                .build();

        // Build customer info
        SaleResponseDTO.CustomerInfo customerInfo = SaleResponseDTO.CustomerInfo.builder()
                .id(sale.getCustomer().getId())
                .name(sale.getCustomer().getName())
                .email(sale.getCustomer().getEmail())
                .build();

        // Build sale response
        return SaleResponseDTO.builder()
                .id(sale.getId())
                .date(sale.getDate())
                .quantity(sale.getQuantity())
                .unitPrice(sale.getUnitPrice())
                .totalPrice(sale.getTotalPrice())
                .status(sale.getStatus())
                .product(productInfo)
                .customer(customerInfo)
                .build();
    }
}
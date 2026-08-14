package io.saadmughal.saleinventorybackend.service;

import io.saadmughal.saleinventorybackend.dto.request.PurchaseRequestDTO;
import io.saadmughal.saleinventorybackend.dto.response.PurchaseResponseDTO;
import io.saadmughal.saleinventorybackend.entity.Product;
import io.saadmughal.saleinventorybackend.entity.Purchase;
import io.saadmughal.saleinventorybackend.entity.PurchaseStatus;
import io.saadmughal.saleinventorybackend.entity.Supplier;
import io.saadmughal.saleinventorybackend.exception.BusinessRuleViolationException;
import io.saadmughal.saleinventorybackend.exception.PurchaseNotFoundException;
import io.saadmughal.saleinventorybackend.exception.SupplierNotFoundException;
import io.saadmughal.saleinventorybackend.repository.ProductRepository;
import io.saadmughal.saleinventorybackend.repository.PurchaseRepository;
import io.saadmughal.saleinventorybackend.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    private Product testProduct;
    private Supplier testSupplier;
    private Purchase testPurchase;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .code("P-1001")
                .name("Wireless Mouse")
                .unitPrice(new BigDecimal("25.99"))
                .currentStock(10)
                .active(true)
                .build();

        testSupplier = Supplier.builder()
                .id(1L)
                .name("Tech Distributors Inc")
                .email("contact@techdist.com")
                .phone("+92-42-11111111")
                .companyName("Tech Distributors")
                .active(true)
                .build();

        testPurchase = Purchase.builder()
                .id(1L)
                .product(testProduct)
                .supplier(testSupplier)
                .date(LocalDateTime.now())
                .quantity(30)
                .unitCost(new BigDecimal("15.00"))
                .totalCost(new BigDecimal("450.00"))
                .status(PurchaseStatus.RECEIVED)
                .build();
    }

    @Test
    @DisplayName("Should create purchase and increase product stock")
    void shouldCreatePurchaseSuccessfully() {
        PurchaseRequestDTO requestDTO = PurchaseRequestDTO.builder()
                .productId(1L)
                .supplierId(1L)
                .quantity(30)
                .unitCost(new BigDecimal("15.00"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(testPurchase);

        PurchaseResponseDTO result = purchaseService.createPurchase(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(30);
        assertThat(result.getTotalCost()).isEqualTo(new BigDecimal("450.00"));
        assertThat(testProduct.getCurrentStock()).isEqualTo(40); // 10 + 30
        verify(productRepository, times(1)).save(testProduct);
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when supplier is inactive")
    void shouldThrowExceptionWhenSupplierIsInactive() {
        testSupplier.setActive(false);
        PurchaseRequestDTO requestDTO = PurchaseRequestDTO.builder()
                .productId(1L)
                .supplierId(1L)
                .quantity(10)
                .unitCost(new BigDecimal("15.00"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));

        assertThatThrownBy(() -> purchaseService.createPurchase(requestDTO))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Supplier (ID: 1) is inactive");

        verify(purchaseRepository, never()).save(any(Purchase.class));
    }

    @Test
    @DisplayName("Should throw PurchaseNotFoundException when purchase ID does not exist")
    void shouldThrowPurchaseNotFoundException() {
        when(purchaseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> purchaseService.getPurchaseById(99L))
                .isInstanceOf(PurchaseNotFoundException.class)
                .hasMessageContaining("Purchase not found with ID: 99");
    }

    @Test
    @DisplayName("Should return purchases by supplier ID")
    void shouldReturnPurchasesBySupplierId() {
        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(purchaseRepository.findBySupplierIdWithDetails(1L)).thenReturn(List.of(testPurchase));

        List<PurchaseResponseDTO> result = purchaseService.getPurchasesBySupplierId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSupplier().getName()).isEqualTo("Tech Distributors Inc");
    }
}

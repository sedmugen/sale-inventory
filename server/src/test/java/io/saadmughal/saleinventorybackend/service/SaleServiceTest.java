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
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private SaleService saleService;

    private Product testProduct;
    private Customer testCustomer;
    private Sale testSale;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .code("P-1001")
                .name("Wireless Mouse")
                .unitPrice(new BigDecimal("25.99"))
                .currentStock(20)
                .active(true)
                .build();

        testCustomer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+92-300-1234567")
                .address("123 Street")
                .blocked(false)
                .build();

        testSale = Sale.builder()
                .id(1L)
                .product(testProduct)
                .customer(testCustomer)
                .date(LocalDateTime.now())
                .quantity(2)
                .unitPrice(new BigDecimal("25.99"))
                .totalPrice(new BigDecimal("51.98"))
                .status(SaleStatus.CONFIRMED)
                .build();
    }

    @Test
    @DisplayName("Should create sale and decrease stock when valid")
    void shouldCreateSaleSuccessfully() {
        SaleRequestDTO requestDTO = SaleRequestDTO.builder()
                .productId(1L)
                .customerId(1L)
                .quantity(2)
                .unitPrice(new BigDecimal("25.99"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(saleRepository.save(any(Sale.class))).thenReturn(testSale);

        SaleResponseDTO result = saleService.createSale(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getStatus()).isEqualTo(SaleStatus.CONFIRMED);
        assertThat(testProduct.getCurrentStock()).isEqualTo(18); // 20 - 2
        verify(productRepository, times(1)).save(testProduct);
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when product is inactive")
    void shouldThrowExceptionWhenProductIsInactive() {
        testProduct.setActive(false);
        SaleRequestDTO requestDTO = SaleRequestDTO.builder()
                .productId(1L)
                .customerId(1L)
                .quantity(1)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThatThrownBy(() -> saleService.createSale(requestDTO))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Product (ID: 1) is inactive");

        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when stock is insufficient")
    void shouldThrowExceptionWhenStockIsInsufficient() {
        SaleRequestDTO requestDTO = SaleRequestDTO.builder()
                .productId(1L)
                .customerId(1L)
                .quantity(50) // current stock is 20
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThatThrownBy(() -> saleService.createSale(requestDTO))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Insufficient stock");

        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when customer is blocked")
    void shouldThrowExceptionWhenCustomerIsBlocked() {
        testCustomer.setBlocked(true);
        SaleRequestDTO requestDTO = SaleRequestDTO.builder()
                .productId(1L)
                .customerId(1L)
                .quantity(1)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        assertThatThrownBy(() -> saleService.createSale(requestDTO))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Customer (ID: 1) is blocked");

        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    @DisplayName("Should throw SaleNotFoundException when sale ID does not exist")
    void shouldThrowSaleNotFoundException() {
        when(saleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.getSaleById(99L))
                .isInstanceOf(SaleNotFoundException.class)
                .hasMessageContaining("Sale not found with ID: 99");
    }

    @Test
    @DisplayName("Should return customer sales history")
    void shouldReturnCustomerSalesHistory() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(saleRepository.findByCustomerIdWithDetails(1L)).thenReturn(List.of(testSale));

        List<SaleResponseDTO> result = saleService.getSalesByCustomerId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomer().getName()).isEqualTo("John Doe");
    }
}

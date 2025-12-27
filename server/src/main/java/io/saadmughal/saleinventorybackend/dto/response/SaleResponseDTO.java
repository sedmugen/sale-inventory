package io.saadmughal.saleinventorybackend.dto.response;

import io.saadmughal.saleinventorybackend.entity.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponseDTO {

    private Long id;
    private LocalDateTime date;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private SaleStatus status;

    // Embedded product information
    private ProductInfo product;

    // Embedded customer information
    private CustomerInfo customer;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInfo {
        private Long id;
        private String code;
        private String name;
        private Integer currentStock;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerInfo {
        private Long id;
        private String name;
        private String email;
    }
}
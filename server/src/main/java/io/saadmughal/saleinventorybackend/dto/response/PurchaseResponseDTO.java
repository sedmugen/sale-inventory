package io.saadmughal.saleinventorybackend.dto.response;

import io.saadmughal.saleinventorybackend.entity.PurchaseStatus;
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
public class PurchaseResponseDTO {

    private Long id;
    private LocalDateTime date;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private PurchaseStatus status;

    // Embedded product information
    private ProductInfo product;

    // Embedded supplier information
    private SupplierInfo supplier;

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
    public static class SupplierInfo {
        private Long id;
        private String name;
        private String companyName;
    }
}
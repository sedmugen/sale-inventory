package io.saadmughal.saleinventorybackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String code;
    private String name;
    private BigDecimal unitPrice;
    private Integer currentStock;
    private Boolean active;
    private ProductDetailResponseDTO productDetail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDetailResponseDTO {
        private Long id;
        private String brand;
        private String category;
        private String description;
        private Integer minStockLevel;
        private Double taxRate;
    }
}
package io.saadmughal.saleinventorybackend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "Product code is required")
    private String code;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price must be non-negative")
    private BigDecimal unitPrice;

    @NotNull(message = "Current stock is required")
    @Min(value = 0, message = "Current stock must be non-negative")
    private Integer currentStock;

    @NotNull(message = "Active status is required")
    private Boolean active;

    @Valid
    private ProductDetailDTO productDetail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductDetailDTO {

        private String brand;

        private String category;

        private String description;

        @NotNull(message = "Minimum stock level is required")
        @Min(value = 0, message = "Minimum stock level must be non-negative")
        @Builder.Default
        private Integer minStockLevel = 0;

        @Min(value = 0, message = "Tax rate must be non-negative")
        @Builder.Default
        private Double taxRate = 0.0;
    }
}
package io.saadmughal.saleinventorybackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Lightweight DTO for product lists
 * Excludes detailed product information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListDTO {

    private Long id;
    private String code;
    private String name;
    private BigDecimal unitPrice;
    private Integer currentStock;
    private Boolean active;
    private String category;
    private String brand;
}
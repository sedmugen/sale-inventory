package io.saadmughal.saleinventorybackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Low Stock Alert feature
 * Contains essential product information and stock levels
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowStockProductDTO {

    private Long id;
    private String code;
    private String name;
    private BigDecimal unitPrice;
    private Integer currentStock;
    private Integer minStockLevel;
    private String category;
    private Boolean active;

    /**
     * Calculated field: how many units below minimum
     */
    private Integer stockDeficit;
}
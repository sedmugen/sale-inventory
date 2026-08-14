package io.saadmughal.saleinventorybackend.dto.request;

import jakarta.validation.constraints.Min;
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
public class SaleRequestDTO {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    /**
     * Unit price can be provided or will default to product's unit price
     * If provided, it allows for custom pricing (discounts, special offers)
     */
    @Min(value = 0, message = "Unit price must be non-negative")
    private BigDecimal unitPrice;
}
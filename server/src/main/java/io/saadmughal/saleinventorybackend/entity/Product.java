package io.saadmughal.saleinventorybackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product code is required")
    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false, length = 200)
    private String name;

    @NotNull(message = "Unit price is required")
    @Min(value = 0, message = "Unit price must be non-negative")
    private BigDecimal unitPrice;

    @NotNull(message = "Current stock is required")
    @Min(value = 0, message = "Current stock must be non-negative")
    @Column(nullable = false)
    private Integer currentStock;

    @NotNull(message = "Active status is required")
    @Column(nullable = false)
    private Boolean active;
}